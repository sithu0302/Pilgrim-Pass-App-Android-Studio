package com.pilgrimpass;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AdminPanelActivity extends AppCompatActivity {

    Button btnViewBookings, btnViewStats;
    TextView tvResult;

    private BookingDbHelper db; // <-- NEW

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panel);

        btnViewBookings = findViewById(R.id.btnViewBookings);
        btnViewStats = findViewById(R.id.btnViewStats);
        tvResult = findViewById(R.id.tvResult);

        db = new BookingDbHelper(this); // <-- NEW

        btnViewBookings.setOnClickListener(v -> showAllBookings());
        btnViewStats.setOnClickListener(v -> showStats());
    }

    private void showAllBookings() {
        StringBuilder sb = new StringBuilder();
        try (Cursor c = db.getAllBookings()) {
            if (c.getCount() == 0) {
                sb.append("No bookings found.");
            } else {
                while (c.moveToNext()) {
                    int id = c.getInt(c.getColumnIndexOrThrow(BookingDbHelper.COL_ID));
                    String date = c.getString(c.getColumnIndexOrThrow(BookingDbHelper.COL_DATE));
                    String time = c.getString(c.getColumnIndexOrThrow(BookingDbHelper.COL_TIME));
                    sb.append("Booking ").append(id).append(": ")
                            .append(date).append(" ").append(time).append("\n");
                }
            }
        }
        tvResult.setText(sb.toString());
    }

    @SuppressLint("SetTextI18n")
    private void showStats() {
        int total = db.getBookingsCount();
        tvResult.setText("Total Bookings: " + total);
    }
}
