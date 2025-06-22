package com.pilgrimpass;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    Button btnBooking, btnQrPass, btnScanPass, btnProfile, btnLiveMap, btnAdminPanel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        btnBooking = findViewById(R.id.btnBooking);
        btnQrPass = findViewById(R.id.btnQrPass);
        btnScanPass = findViewById(R.id.btnScanPass);
        btnProfile = findViewById(R.id.btnProfile);
        btnLiveMap = findViewById(R.id.btnLiveMap);
        btnAdminPanel = findViewById(R.id.btnAdminPanel);

        btnBooking.setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this, BookingActivity.class)));

        btnQrPass.setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this, QrPassActivity.class)));

        btnScanPass.setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this, ScanActivity.class)));

        btnProfile.setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this, ProfileActivity.class)));

        btnLiveMap.setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this, MapActivity.class)));

        btnAdminPanel.setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this, AdminPanelActivity.class)));
    }
}
