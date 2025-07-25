package com.pilgrimpass;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    Button btnBooking, btnQrPass, btnScanPass, btnLiveMap, btnAdminPanel;
    Button btnDaladaMaligawaInfo; // Profile බොත්තම සඳහා වූ විචල්‍යය
    Button btnGalleryCategories; // <--- NEW BUTTON DECLARATION (ගැලරි බොත්තම සඳහා)
    TextView tvAdminWelcome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Login තත්ත්වය පරීක්ෂා කරන්න.
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean("isLoggedIn", false);

        if (!isLoggedIn) {
            // Login වී නොමැති නම් login screen වෙත යොමු කරන්න.
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.activity_home); // home screen layout එක සකසන්න

        // UI elements Initialize කරන්න
        btnBooking = findViewById(R.id.btnBooking);
        btnQrPass = findViewById(R.id.btnQrPass);
        btnScanPass = findViewById(R.id.btnScanPass);
        btnDaladaMaligawaInfo = findViewById(R.id.btnProfile); // activity_home.xml හි Profile බොත්තමේ ID එක භාවිතා කරයි
        btnLiveMap = findViewById(R.id.btnLiveMap);
        btnAdminPanel = findViewById(R.id.btnAdminPanel);
        tvAdminWelcome = findViewById(R.id.tvAdminWelcome);
       // <--- NEW BUTTON INITIALIZATION (අවශ්‍ය නම්)

        // ඔබේ settings බොත්තම header එකේ තිබේ නම්
        Button btnSettings = findViewById(R.id.button);

        String userType = prefs.getString("loggedInUserType", "Public");

        // --- Role-based access control logic ---
        if (userType.equals("Admin")) {
            btnScanPass.setVisibility(View.VISIBLE);
            btnAdminPanel.setVisibility(View.VISIBLE);
            btnQrPass.setVisibility(View.VISIBLE);
            tvAdminWelcome.setVisibility(View.VISIBLE);
        } else {
            btnScanPass.setVisibility(View.GONE);
            btnAdminPanel.setVisibility(View.GONE);
            btnQrPass.setVisibility(View.VISIBLE);
            tvAdminWelcome.setVisibility(View.GONE);
        }
        // --- End of Role-based access control logic ---

        // එක් එක් බොත්තම සඳහා OnClickListener සකසන්න.
        btnBooking.setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this, BookingActivity.class)));

        btnQrPass.setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this, QrPassActivity.class)));

        btnScanPass.setOnClickListener(v -> {
            if (userType.equals("Admin")) {
                startActivity(new Intent(HomeActivity.this, ScanActivity.class));
            }
        });

        // **වැදගත්: මෙය "Profile" බොත්තම DaladaMaligawaInfoActivity වෙත යොමු කරන වෙනසයි**
        btnDaladaMaligawaInfo.setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this, DaladaMaligawaInfoActivity.class)));

        btnLiveMap.setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this, MapActivity.class)));

        btnAdminPanel.setOnClickListener(v -> {
            if (userType.equals("Admin")) {
                startActivity(new Intent(HomeActivity.this, AdminPanelActivity.class));
            }
        });

        btnSettings.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, SettingActivity.class);
            startActivity(intent);
        });

        // <--- අලුත්: Gallery Categories බොත්තම සඳහා OnClickListener (පියවර 4 හිදී එකතු කරන්නේ නම්)
        if(btnGalleryCategories != null) { // බොත්තම HomeActivity layout එකේ ඇත්නම් පමණක්
            btnGalleryCategories.setOnClickListener(v -> {
                Intent intent = new Intent(HomeActivity.this, GalleryCategoryActivity.class);
                startActivity(intent);
            });
        }
    }
}