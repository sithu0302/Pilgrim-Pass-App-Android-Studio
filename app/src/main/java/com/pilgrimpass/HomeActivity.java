package com.pilgrimpass;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button; // android.widget.Button import එක දැනටමත් තිබේ.
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    Button btnBooking, btnQrPass, btnScanPass, btnProfile, btnLiveMap, btnAdminPanel;
    TextView tvAdminWelcome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check login status first.
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean("isLoggedIn", false);

        if (!isLoggedIn) {
            // Redirect to login screen if not logged in.
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.activity_home);

        btnBooking = findViewById(R.id.btnBooking);
        btnQrPass = findViewById(R.id.btnQrPass);
        btnScanPass = findViewById(R.id.btnScanPass);
        btnProfile = findViewById(R.id.btnProfile);
        btnLiveMap = findViewById(R.id.btnLiveMap);
        btnAdminPanel = findViewById(R.id.btnAdminPanel);
        tvAdminWelcome = findViewById(R.id.tvAdminWelcome);

        // Settings Button එක find කරන්න
        // Settings Button එක සඳහා ප්‍රකාශනය
        // ඔබගේ settings button එකේ id එක 'button' නිසා 'btnSettings' ලෙස නම් කරමු.
        Button btnSettings = findViewById(R.id.button); // XML හි id="button" ලෙස ඇති නිසා

        // Get the userType from SharedPreferences that was saved during login.
        String userType = prefs.getString("loggedInUserType", "Public"); // Default to "Public".

        // --- Role-based access control logic for button visibility ---
        if (userType.equals("Admin")) {
            // If the user is Admin, show relevant buttons and the welcome message.
            btnScanPass.setVisibility(View.VISIBLE);
            btnAdminPanel.setVisibility(View.VISIBLE);
            btnQrPass.setVisibility(View.VISIBLE); // Admins can also generate QR passes.
            tvAdminWelcome.setVisibility(View.VISIBLE); // Show "Welcome, Administrator!" message.
        } else {
            // If the user is Public, hide admin-specific buttons and the welcome message.
            btnScanPass.setVisibility(View.GONE); // Hide QR Pass Scan.
            btnAdminPanel.setVisibility(View.GONE); // Hide Admin Panel.
            btnQrPass.setVisibility(View.VISIBLE); // Public users should see their QR pass button.
            tvAdminWelcome.setVisibility(View.GONE); // Hide admin welcome message.
        }
        // --- End of Role-based access control logic ---

        // Set up OnClickListener for each button.
        btnBooking.setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this, BookingActivity.class)));

        btnQrPass.setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this, QrPassActivity.class)));

        btnScanPass.setOnClickListener(v -> {
            // Only allow Admins to scan passes.
            if (userType.equals("Admin")) {
                startActivity(new Intent(HomeActivity.this, ScanActivity.class));
            }
        });

        btnProfile.setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this, ProfileActivity.class)));

        btnLiveMap.setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this, MapActivity.class)));

        btnAdminPanel.setOnClickListener(v -> {
            // Only allow Admins to access the Admin Panel.
            if (userType.equals("Admin")) {
                startActivity(new Intent(HomeActivity.this, AdminPanelActivity.class));
            }
        });

        // Settings Button එකට OnClickListener එකක් සකසන්න
        btnSettings.setOnClickListener(v -> {
            // SettingsActivity එකට යාමට Intent එකක් සාදන්න
            Intent intent = new Intent(HomeActivity.this, SettingActivity.class);
            startActivity(intent); // Activity එක ආරම්භ කරන්න
        });
    }
}