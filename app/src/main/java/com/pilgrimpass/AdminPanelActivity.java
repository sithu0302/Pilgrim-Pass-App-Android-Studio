package com.pilgrimpass;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AdminPanelActivity extends AppCompatActivity {

    Button btnViewBookings, btnViewStats;
    TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panel);

        btnViewBookings = findViewById(R.id.btnViewBookings);
        btnViewStats = findViewById(R.id.btnViewStats);
        tvResult = findViewById(R.id.tvResult);

        btnViewBookings.setOnClickListener(v -> {
            // TODO: Replace with real bookings fetch & display logic
            tvResult.setText("Booking 1: User A - 2025-06-21 09:00\nBooking 2: User B - 2025-06-21 10:00");
        });

        btnViewStats.setOnClickListener(v -> {
            // TODO: Replace with real stats fetch & display logic
            tvResult.setText("Total Bookings: 10\nAvailable Slots: 5");
        });
    }
}
