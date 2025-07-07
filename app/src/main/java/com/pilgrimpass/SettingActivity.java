package com.pilgrimpass;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class SettingActivity extends AppCompatActivity {

    private Switch switchWeatherDetails;
    private Switch switchNotifications;

    private SharedPreferences settingsPrefs;
    private static final String PREFS_NAME = "AppSettings";
    private static final String KEY_WEATHER_DETAILS = "showWeatherDetails";
    private static final String KEY_NOTIFICATIONS = "enableNotifications";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        settingsPrefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        switchWeatherDetails = findViewById(R.id.switchWeatherDetails);
        switchNotifications = findViewById(R.id.switchNotifications);
        MaterialButton btnLanguageSettings = findViewById(R.id.btnLanguageSettings);
        MaterialButton btnProfileSettings = findViewById(R.id.btnProfileSettings);
        MaterialButton btnLogout = findViewById(R.id.btnLogout);

        loadSettings();

        switchWeatherDetails.setOnCheckedChangeListener((buttonView, isChecked) -> {
            settingsPrefs.edit().putBoolean(KEY_WEATHER_DETAILS, isChecked).apply();
            Toast.makeText(this, "Weather Details: " + (isChecked ? "ON" : "OFF"), Toast.LENGTH_SHORT).show();
        });

        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            settingsPrefs.edit().putBoolean(KEY_NOTIFICATIONS, isChecked).apply();
            Toast.makeText(this, "Notifications: " + (isChecked ? "Enabled" : "Disabled"), Toast.LENGTH_SHORT).show();
        });

        btnLanguageSettings.setOnClickListener(v -> {
            Toast.makeText(this, "Language Settings not implemented yet", Toast.LENGTH_SHORT).show();
        });

        btnProfileSettings.setOnClickListener(v -> {
            // Navigate to ProfileSettingActivity (note capitalization)
            Intent intent = new Intent(SettingActivity.this, ProfileSettingActivity.class);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> {
            SharedPreferences userPrefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            userPrefs.edit().putBoolean("isLoggedIn", false).putString("loggedInUserType", "Public").apply();

            Intent intent = new Intent(SettingActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();

            Toast.makeText(this, "Logged out successfully.", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadSettings() {
        boolean showWeather = settingsPrefs.getBoolean(KEY_WEATHER_DETAILS, true);
        boolean enableNotifications = settingsPrefs.getBoolean(KEY_NOTIFICATIONS, true);

        switchWeatherDetails.setChecked(showWeather);
        switchNotifications.setChecked(enableNotifications);
    }
}
