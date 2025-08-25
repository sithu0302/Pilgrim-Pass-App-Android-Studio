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
import java.util.Locale;

public class BookingActivity extends AppCompatActivity {

    private TextView txtSelectedDate, txtSelectedTime;

    private String selectedDateIso = null; // YYYY-MM-DD
    private String selectedTime24 = null;  // HH:mm

    private BookingDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking); // your first XML

        dbHelper = new BookingDbHelper(this);

        txtSelectedDate = findViewById(R.id.txtSelectedDate);
        txtSelectedTime = findViewById(R.id.txtSelectedTime);
        Button btnSelectDate = findViewById(R.id.btnSelectDate);
        Button btnSelectTime = findViewById(R.id.btnSelectTime);
        Button btnConfirmBooking = findViewById(R.id.btnConfirmBooking);

        btnSelectDate.setOnClickListener(v -> openDatePicker());
        btnSelectTime.setOnClickListener(v -> openTimePicker());
        btnConfirmBooking.setOnClickListener(v -> saveBooking());
    }

    private void openDatePicker() {
        final Calendar cal = Calendar.getInstance();
        int y = cal.get(Calendar.YEAR);
        int m = cal.get(Calendar.MONTH);
        int d = cal.get(Calendar.DAY_OF_MONTH);

        @SuppressLint("SetTextI18n") DatePickerDialog dlg = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    // Month +1 because Calendar months are 0-based
                    selectedDateIso = String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, dayOfMonth);
                    txtSelectedDate.setText("Selected date: " + selectedDateIso);
                },
                y, m, d
        );
        dlg.show();
    }

    private void openTimePicker() {
        final Calendar cal = Calendar.getInstance();
        int h = cal.get(Calendar.HOUR_OF_DAY);
        int min = cal.get(Calendar.MINUTE);

        @SuppressLint("SetTextI18n") TimePickerDialog dlg = new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> {
                    selectedTime24 = String.format(Locale.US, "%02d:%02d", hourOfDay, minute);
                    txtSelectedTime.setText("Selected time: " + selectedTime24);
                },
                h, min, true // 24-hour view
        );
        dlg.show();
    }

    private void saveBooking() {
        if (selectedDateIso == null) {
            Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedTime24 == null) {
            Toast.makeText(this, "Please select a time", Toast.LENGTH_SHORT).show();
            return;
        }

        long rowId = dbHelper.insertBooking(selectedDateIso, selectedTime24);
        if (rowId > 0) {
            Toast.makeText(this, "Booking saved (ID: " + rowId + ")", Toast.LENGTH_LONG).show();
            // reset UI
            selectedDateIso = null;
            selectedTime24 = null;
            txtSelectedDate.setText(getString(R.string.selected_date_not_set));
            txtSelectedTime.setText(getString(R.string.selected_time_not_set));
        } else {
            Toast.makeText(this, "Failed to save booking", Toast.LENGTH_LONG).show();
        }
    }
}
