package com.pilgrimpass;

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
        try {
            String data = "BookingID12345|Date:2025-06-21|Time:09:00 AM";
            BarcodeEncoder encoder = new BarcodeEncoder();
            Bitmap bitmap = encoder.encodeBitmap(data, BarcodeFormat.QR_CODE, 500, 500);
            qrImage.setImageBitmap(bitmap);
        } catch (Exception e) {
            Toast.makeText(this, "Error generating QR: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
