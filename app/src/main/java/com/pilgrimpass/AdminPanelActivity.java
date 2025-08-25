package com.pilgrimpass;

import android.database.Cursor;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class AdminPanelActivity extends AppCompatActivity {

    private TextView tvResult;
    private BookingDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panel); // your second XML

        dbHelper = new BookingDbHelper(this);

        Button btnViewBookings = findViewById(R.id.btnViewBookings);
        Button btnViewStats = findViewById(R.id.btnViewStats);
        tvResult = findViewById(R.id.tvResult);
        tvResult.setMovementMethod(new ScrollingMovementMethod());

        btnViewBookings.setOnClickListener(v -> showAllBookings());
        btnViewStats.setOnClickListener(v -> showStats());
    }

    private void showAllBookings() {
        StringBuilder sb = new StringBuilder();
        Cursor c = dbHelper.getAllBookings();
        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    long id = c.getLong(c.getColumnIndexOrThrow(BookingDbHelper.COL_ID));
                    String date = c.getString(c.getColumnIndexOrThrow(BookingDbHelper.COL_DATE));
                    String time = c.getString(c.getColumnIndexOrThrow(BookingDbHelper.COL_TIME));
                    sb.append("ID: ").append(id)
                            .append(" | Date: ").append(date)
                            .append(" | Time: ").append(time)
                            .append("\n");
                } while (c.moveToNext());
            } else {
                sb.append("No bookings found.\n");
            }
            c.close();
        } else {
            sb.append("Query failed.\n");
        }
        tvResult.setText(sb.toString());
    }

    private void showStats() {
        StringBuilder sb = new StringBuilder();
        int total = dbHelper.getTotalCount();
        sb.append("Total bookings: ").append(total).append("\n\n");

        sb.append("Bookings by date:\n");
        Cursor c = dbHelper.getCountsByDate();
        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    String date = c.getString(0);
                    int cnt = c.getInt(1);
                    sb.append("• ").append(date).append(" : ").append(cnt).append("\n");
                } while (c.moveToNext());
            } else {
                sb.append("(none)\n");
            }
            c.close();
        } else {
            sb.append("(failed)\n");
        }
        tvResult.setText(sb.toString());
    }
}
