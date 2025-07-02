package com.pilgrimpass;

import android.content.SharedPreferences; // Import for SharedPreferences.
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class QrPassActivity extends AppCompatActivity {

    ImageView qrImage;
    Button btnGenerateQR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_pass);

        qrImage = findViewById(R.id.qrImage);
        btnGenerateQR = findViewById(R.id.btnGenerateQR);

        btnGenerateQR.setOnClickListener(v -> generateQRCode());
    }

    private void generateQRCode() {
        // 1. Retrieve user data from SharedPreferences.
        SharedPreferences userPrefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        // "BookingDetails" is the SharedPreferences file where you save booking data.
        SharedPreferences bookingPrefs = getSharedPreferences("BookingDetails", MODE_PRIVATE);

        // Get the username of the logged-in user.
        String username = userPrefs.getString("username", "GuestUser");

        // Get the booking details saved from the BookingActivity.
        String selectedDate = bookingPrefs.getString("selectedBookingDate", "NoDateSelected");
        String selectedTime = bookingPrefs.getString("selectedBookingTime", "NoTimeSelected");

        // Do not generate QR if booking details are missing.
        if (selectedDate.equals("NoDateSelected") || selectedTime.equals("NoTimeSelected")) {
            Toast.makeText(this, "Please complete a booking before generating a QR code.", Toast.LENGTH_LONG).show();
            return;
        }

        // 2. Prepare a unique data string for the QR code.
        // This data string will vary per user.
        String qrData = "User:" + username +
                "|Date:" + selectedDate +
                "|Time:" + selectedTime +
                "|Generated:" + System.currentTimeMillis(); // Add a timestamp for uniqueness.

        try {
            // 3. Generate the QR code using the prepared data string.
            BarcodeEncoder encoder = new BarcodeEncoder();
            Bitmap bitmap = encoder.encodeBitmap(qrData, BarcodeFormat.QR_CODE, 500, 500);
            qrImage.setImageBitmap(bitmap);
            Toast.makeText(this, "QR Code Generated for " + username + ".", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Error generating QR: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace(); // Log error details.
        }
    }
}
