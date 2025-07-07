package com.pilgrimpass;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.Calendar;

public class ProfileSettingActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView imgProfilePic;
    private EditText etName, etEmail, etPhone, etDOB;

    private SharedPreferences sharedPreferences;
    private Uri imageUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setting); // Rename your layout accordingly

        imgProfilePic = findViewById(R.id.imgProfilePic);
        Button btnChangePic = findViewById(R.id.btnChangePic);
        Button btnSaveProfile = findViewById(R.id.btnSaveProfile);
        Button btnResetProfile = findViewById(R.id.btnResetProfile);
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etDOB = findViewById(R.id.etDOB);

        sharedPreferences = getSharedPreferences("UserProfile", MODE_PRIVATE);

        loadUserInfo();

        btnChangePic.setOnClickListener(v -> openImageChooser());

        etDOB.setOnClickListener(v -> showDatePicker());

        btnSaveProfile.setOnClickListener(v -> {
            if (validateInput()) {
                saveUserInfo();
            }
        });

        btnResetProfile.setOnClickListener(v -> resetUserInfo());
    }

    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Profile Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                imgProfilePic.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, month1, dayOfMonth) -> {
                    @SuppressLint("DefaultLocale") String date = String.format("%02d/%02d/%d", dayOfMonth, month1 + 1, year1);
                    etDOB.setText(date);
                }, year, month, day);

        datePickerDialog.show();
    }

    private boolean validateInput() {
        if (TextUtils.isEmpty(etName.getText())) {
            etName.setError("Name is required");
            return false;
        }
        if (TextUtils.isEmpty(etEmail.getText())) {
            etEmail.setError("Email is required");
            return false;
        }
        if (TextUtils.isEmpty(etPhone.getText())) {
            etPhone.setError("Phone number is required");
            return false;
        }
        if (TextUtils.isEmpty(etDOB.getText())) {
            etDOB.setError("Date of Birth is required");
            return false;
        }
        return true;
    }

    private void saveUserInfo() {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("name", etName.getText().toString());
        editor.putString("email", etEmail.getText().toString());
        editor.putString("phone", etPhone.getText().toString());
        editor.putString("dob", etDOB.getText().toString());

        if (imageUri != null) {
            editor.putString("profilePicUri", imageUri.toString());
        }

        editor.apply();

        Toast.makeText(this, "Profile saved successfully", Toast.LENGTH_SHORT).show();
    }

    private void loadUserInfo() {
        etName.setText(sharedPreferences.getString("name", ""));
        etEmail.setText(sharedPreferences.getString("email", ""));
        etPhone.setText(sharedPreferences.getString("phone", ""));
        etDOB.setText(sharedPreferences.getString("dob", ""));

        String profilePicString = sharedPreferences.getString("profilePicUri", null);
        if (profilePicString != null) {
            imageUri = Uri.parse(profilePicString);
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                imgProfilePic.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
                imgProfilePic.setImageResource(R.drawable.ic_baseline_person_24);
            }
        } else {
            imgProfilePic.setImageResource(R.drawable.ic_baseline_person_24);
        }
    }

    private void resetUserInfo() {
        etName.setText("");
        etEmail.setText("");
        etPhone.setText("");
        etDOB.setText("");
        imgProfilePic.setImageResource(R.drawable.ic_baseline_person_24);
        imageUri = null;

        sharedPreferences.edit().clear().apply();

        Toast.makeText(this, "Profile fields reset.", Toast.LENGTH_SHORT).show();
    }
}
