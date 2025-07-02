package com.pilgrimpass; // Make sure this matches your actual package name

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
// For the Logout button if it's a standard Button
import android.widget.Switch; // For the Switch components
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton; // For MaterialButtons

public class SettingActivity extends AppCompatActivity {

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch switchWeatherDetails;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch switchNotifications;

    // SharedPreferences for saving settings state
    private SharedPreferences settingsPrefs;
    private static final String PREFS_NAME = "AppSettings";
    private static final String KEY_WEATHER_DETAILS = "showWeatherDetails";
    private static final String KEY_NOTIFICATIONS = "enableNotifications";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        // Initialize SharedPreferences
        settingsPrefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Initialize UI elements
        // UI Elements from activity_settings.xml
        switchWeatherDetails = findViewById(R.id.switchWeatherDetails);
        switchNotifications = findViewById(R.id.switchNotifications);
        MaterialButton btnLanguageSettings = findViewById(R.id.btnLanguageSettings);
        MaterialButton btnProfileSettings = findViewById(R.id.btnProfileSettings);
        MaterialButton btnLogout = findViewById(R.id.btnLogout);

        // Load saved settings state and set Switch positions
        loadSettings();

        // Set up Listeners for Switches
        switchWeatherDetails.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Save the new state of the switch to SharedPreferences
            SharedPreferences.Editor editor = settingsPrefs.edit();
            editor.putBoolean(KEY_WEATHER_DETAILS, isChecked);
            editor.apply();
            Toast.makeText(SettingActivity.this, "Weather Details: " + (isChecked ? "ON" : "OFF"), Toast.LENGTH_SHORT).show();

            // TODO: Add logic here to actually show/hide weather details on relevant screens
            // This would typically involve sending a broadcast or updating a global state
            // that other activities can listen to.
        });

        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Save the new state of the switch to SharedPreferences
            SharedPreferences.Editor editor = settingsPrefs.edit();
            editor.putBoolean(KEY_NOTIFICATIONS, isChecked);
            editor.apply();
            Toast.makeText(SettingActivity.this, "Notifications: " + (isChecked ? "Enabled" : "Disabled"), Toast.LENGTH_SHORT).show();

            // TODO: Add logic here to enable/disable actual notifications
            // This might involve managing BroadcastReceivers or Firebase Cloud Messaging subscriptions.
        });

        // Set up Click Listeners for MaterialButtons
        btnLanguageSettings.setOnClickListener(v -> {
            // TODO: Navigate to Language Settings Activity
            Toast.makeText(SettingActivity.this, "Opening Language Settings...", Toast.LENGTH_SHORT).show();
            // Example: startActivity(new Intent(SettingsActivity.this, LanguageSettingsActivity.class));
        });

        btnProfileSettings.setOnClickListener(v -> {
            // TODO: Navigate to Profile Settings Activity
            Toast.makeText(SettingActivity.this, "Opening Profile Settings...", Toast.LENGTH_SHORT).show();
            // Example: startActivity(new Intent(SettingsActivity.this, ProfileSettingsActivity.class));
        });

        btnLogout.setOnClickListener(v -> {
            // Clear login status from SharedPreferences
            SharedPreferences userPrefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            SharedPreferences.Editor userEditor = userPrefs.edit();
            userEditor.putBoolean("isLoggedIn", false); // Set loggedIn status to false
            userEditor.putString("loggedInUserType", "Public"); // Reset user type to default
            userEditor.apply();

            // Redirect to LoginActivity (or the very first activity of your app)
            Intent intent = new Intent(SettingActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear back stack
            startActivity(intent);
            finish(); // Finish SettingsActivity
            Toast.makeText(SettingActivity.this, "Logged out successfully.", Toast.LENGTH_SHORT).show();
        });
    }

    // Method to load saved settings from SharedPreferences
    private void loadSettings() {
        boolean showWeather = settingsPrefs.getBoolean(KEY_WEATHER_DETAILS, true); // Default to true
        boolean enableNotifications = settingsPrefs.getBoolean(KEY_NOTIFICATIONS, true); // Default to true

        switchWeatherDetails.setChecked(showWeather);
        switchNotifications.setChecked(enableNotifications);
    }
}