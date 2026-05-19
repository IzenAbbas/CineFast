package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private SessionManager sessionManager;
    private DatabaseReference usersReference;
    private EditText nameInput;
    private EditText emailInput;
    private EditText passwordInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        auth = FirebaseAuth.getInstance();
        sessionManager = new SessionManager(this);
        usersReference = FirebaseDatabase.getInstance().getReference("users");

        nameInput = findViewById(R.id.signup_name);
        emailInput = findViewById(R.id.signup_email);
        passwordInput = findViewById(R.id.signup_password);

        ImageButton backButton = findViewById(R.id.signup_back_button);
        Button signupButton = findViewById(R.id.signup_button);

        backButton.setOnClickListener(v -> finish());
        signupButton.setOnClickListener(v -> attemptSignup());
    }

    private void attemptSignup() {
        String name = nameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (!isSignupInputValid(name, email, password)) {
            return;
        }

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        saveUserAndContinue(name, email);
                    } else {
                        Toast.makeText(this, getString(R.string.signup_failed, getErrorMessage(task.getException())), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private boolean isSignupInputValid(String name, String email, String password) {
        if (TextUtils.isEmpty(name)) {
            nameInput.setError(getString(R.string.error_name_required));
            nameInput.requestFocus();
            return false;
        }

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

        if (password.length() < 8) {
            passwordInput.setError(getString(R.string.error_password_length));
            passwordInput.requestFocus();
            return false;
        }

        return true;
    }

    private void saveUserAndContinue(String name, String email) {
        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, R.string.error_unknown, Toast.LENGTH_LONG).show();
            return;
        }

        String userId = auth.getCurrentUser().getUid();
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("name", name);
        userMap.put("email", email);

        usersReference.child(userId)
                .setValue(userMap)
                .addOnSuccessListener(unused -> {
                    FirebaseUser user = auth.getCurrentUser();
                    if (user != null) {
                        user.sendEmailVerification()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), 
                                        "Verification email sent. Please check your inbox.", Toast.LENGTH_SHORT).show();
                                    auth.signOut();
                                    finish(); // Go back to login
                                } else {
                                    Toast.makeText(getApplicationContext(), 
                                        "Error sending verification email.", Toast.LENGTH_SHORT).show();
                                }
                            });
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, getString(R.string.user_save_failed, getErrorMessage(e)), Toast.LENGTH_LONG).show());
    }

    private void openMainScreen() {
        Intent intent = new Intent(SignupActivity.this, MainActivity.class);
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
