package com.pilgrimpass;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class BookingActivity extends AppCompatActivity {

    TextView txtSelectedDate, txtSelectedTime;
    Button btnSelectDate, btnSelectTime, btnConfirmBooking;

    String selectedDate = "", selectedTime = "";
    private BookingDbHelper db; // <-- NEW

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        txtSelectedDate = findViewById(R.id.txtSelectedDate);
        txtSelectedTime = findViewById(R.id.txtSelectedTime);
        btnSelectDate = findViewById(R.id.btnSelectDate);
        btnSelectTime = findViewById(R.id.btnSelectTime);
        btnConfirmBooking = findViewById(R.id.btnConfirmBooking);

        db = new BookingDbHelper(this); // <-- NEW (ensures table exists)

        btnSelectDate.setOnClickListener(v -> showDatePicker());
        btnSelectTime.setOnClickListener(v -> showTimePicker());

        btnConfirmBooking.setOnClickListener(v -> {
            if (!selectedDate.isEmpty() && !selectedTime.isEmpty()) {

                long rowId = db.insertBooking(selectedDate, selectedTime); // <-- SAVE TO SQLite
                if (rowId > 0) {
                    Toast.makeText(this,
                            "Booking saved (ID " + rowId + ") on " + selectedDate + " at " + selectedTime,
                            Toast.LENGTH_LONG).show();

                    // Optional: clear UI
                } else {
                    Toast.makeText(this, "Failed to save booking.", Toast.LENGTH_SHORT).show();
                }

            } else {
                Toast.makeText(this, "Please select date and time.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        @SuppressLint("SetTextI18n") DatePickerDialog dialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
                    txtSelectedDate.setText("Selected Date: " + selectedDate);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();
        @SuppressLint({"SetTextI18n", "DefaultLocale"}) TimePickerDialog dialog = new TimePickerDialog(this,
                (view, hourOfDay, minute) -> {
                    selectedTime = String.format("%02d:%02d", hourOfDay, minute);
                    txtSelectedTime.setText("Selected Time: " + selectedTime);
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true);
        dialog.show();
    }
}
