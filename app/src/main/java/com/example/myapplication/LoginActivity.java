package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.credentials.CredentialManager;
import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.CustomCredential;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.exceptions.GetCredentialException;

import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private CredentialManager credentialManager;
    private static final String TAG = "GoogleSignIn";

    private SessionManager sessionManager;
    private EditText emailInput;
    private EditText passwordInput;
    private CheckBox rememberMe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();
        credentialManager = CredentialManager.create(this);
        sessionManager = new SessionManager(this);

        FirebaseUser user = auth.getCurrentUser();
        if (user != null && sessionManager.isLoggedIn()) {
            user.reload().addOnCompleteListener(task -> {
                if (user.isEmailVerified()) {
                    openMainScreen();
                } else {
                    Toast.makeText(this, "Please verify your email to continue.", Toast.LENGTH_SHORT).show();
                    auth.signOut();
                }
            });
            return;
        }

        emailInput = findViewById(R.id.login_email);
        passwordInput = findViewById(R.id.login_password);
        rememberMe = findViewById(R.id.login_remember_me);

        ImageButton backButton = findViewById(R.id.login_back_button);
        Button loginButton = findViewById(R.id.login_button);
        TextView goToSignup = findViewById(R.id.login_register_link);
        TextView forgotPassword = findViewById(R.id.login_forgot_password);

        backButton.setOnClickListener(v -> finish());

        goToSignup.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, SignupActivity.class)));

        loginButton.setOnClickListener(v -> attemptLogin());

        forgotPassword.setOnClickListener(v -> resetPassword());

        findViewById(R.id.btn_google_login).setOnClickListener(v -> signInWithGoogle());
    }

    private void resetPassword() {
        String emailAddress = emailInput.getText().toString().trim();
        if (TextUtils.isEmpty(emailAddress)) {
            emailInput.setError("Enter your email address to reset password");
            emailInput.requestFocus();
            return;
        }

        auth.sendPasswordResetEmail(emailAddress)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(),
                        "Reset link sent to your email!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(),
                        "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            });
    }

    private void signInWithGoogle() {
        GetGoogleIdOption googleIdOption = new GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId("304959669914-gm5btnth003hcuhc3lr7j6p7v8kb9q1l.apps.googleusercontent.com")
                .setAutoSelectEnabled(true)
                .build();

        GetCredentialRequest request = new GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build();

        credentialManager.getCredentialAsync(
                this,
                request,
                new android.os.CancellationSignal(),
                Executors.newSingleThreadExecutor(),
                new CredentialManagerCallback<GetCredentialResponse, GetCredentialException>() {
                    @Override
                    public void onResult(GetCredentialResponse result) {
                        handleSignInResult(result);
                    }

                    @Override
                    public void onError(@NonNull GetCredentialException e) {
                        Log.e(TAG, "Sign in failed: " + e.getMessage());
                        runOnUiThread(() -> Toast.makeText(LoginActivity.this, "Sign in failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    }
                }
        );
    }

    private void handleSignInResult(GetCredentialResponse result) {
        if (result.getCredential() instanceof CustomCredential) {
            CustomCredential credential = (CustomCredential) result.getCredential();

            if (credential.getType().equals(GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL)) {
                try {
                    GoogleIdTokenCredential googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.getData());
                    String idToken = googleIdTokenCredential.getIdToken();

                    firebaseAuthWithGoogle(idToken);
                } catch (Exception e) {
                    Log.e(TAG, "Error parsing credential", e);
                }
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);

        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        runOnUiThread(() -> {
                            Toast.makeText(LoginActivity.this, "Welcome " + user.getDisplayName(), Toast.LENGTH_SHORT).show();
                            sessionManager.setLoggedIn(true);
                            openMainScreen();
                        });
                    } else {
                        Log.w(TAG, "firebaseAuthWithGoogle:failure", task.getException());
                        runOnUiThread(() -> Toast.makeText(LoginActivity.this, "Firebase Auth Failed", Toast.LENGTH_SHORT).show());
                    }
                });
    }

    private void attemptLogin() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (!isLoginInputValid(email, password)) {
            return;
        }

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            user.reload().addOnCompleteListener(reloadTask -> {
                                if (user.isEmailVerified()) {
                                    sessionManager.setLoggedIn(rememberMe.isChecked());
                                    if (!rememberMe.isChecked()) {
                                        Toast.makeText(this, R.string.remember_me_hint, Toast.LENGTH_SHORT).show();
                                    }
                                    openMainScreen();
                                } else {
                                    Toast.makeText(getApplicationContext(),
                                        "Please verify your email to continue.", Toast.LENGTH_SHORT).show();
                                    auth.signOut();
                                }
                            });
                        }
                    } else {
                        Toast.makeText(this, getString(R.string.login_failed, getErrorMessage(task.getException())), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private boolean isLoginInputValid(String email, String password) {
        if (TextUtils.isEmpty(email)) {
            emailInput.setError(getString(R.string.error_email_required));
            emailInput.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            passwordInput.setError(getString(R.string.error_password_required));
            passwordInput.requestFocus();
            return false;
        }

        return true;
    }

    private void openMainScreen() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private String getErrorMessage(Exception exception) {
        if (exception == null || TextUtils.isEmpty(exception.getMessage())) {
            return getString(R.string.error_unknown);
        }
        return exception.getMessage();
    }
}
