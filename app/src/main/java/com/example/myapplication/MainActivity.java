package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SessionManager sessionManager = new SessionManager(this);
        if (FirebaseAuth.getInstance().getCurrentUser() == null || !sessionManager.isLoggedIn()) {
            openLoginScreen();
            return;
        }

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        View mainView = findViewById(R.id.main_fragment_container);
        ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.nav_home) {
                    replaceFragment(new HomeFragment());
                } else if (id == R.id.nav_my_bookings) {
                    replaceFragment(new MyBookingsFragment());
                } else if (id == R.id.nav_last_booking) {
                    viewLastBooking();
                } else if (id == R.id.nav_logout) {
                    logout();
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.main_fragment_container, new HomeFragment())
                    .commit();
        }
    }

    private void viewLastBooking() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.main_fragment_container);
        if (currentFragment instanceof HomeFragment) {
            ((HomeFragment) currentFragment).viewLastBooking();
        } else {
            // If not on HomeFragment, we could either navigate to Home and then show,
            // or just replace with HomeFragment and pass a flag.
            // For simplicity, let's just replace and then show.
            HomeFragment homeFragment = new HomeFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_fragment_container, homeFragment)
                    .commitNow(); // commitNow to ensure it's added immediately
            homeFragment.viewLastBooking();
        }
    }

    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_fragment_container, fragment)
                .commit();
    }

    private void logout() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.logout_confirm_title)
                .setMessage(R.string.logout_confirm_message)
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    FirebaseAuth.getInstance().signOut();
                    SessionManager sessionManager = new SessionManager(this);
                    sessionManager.clearSession();
                    openLoginScreen();
                })
                .setNegativeButton(R.string.no, null)
                .show();
    }

    public void openDrawer() {
        if (drawerLayout != null) {
            drawerLayout.openDrawer(GravityCompat.START);
        }
    }

    private void openLoginScreen() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
