package com.pilgrimpass;

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

public class profileSettingActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView imgProfilePic;
    private EditText etName, etEmail, etPhone, etDOB;

    private SharedPreferences sharedPreferences;
    private Uri imageUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dalada_maligawa_info); // This is likely your profile settings layout

        imgProfilePic = findViewById(R.id.imgProfilePic);
        Button btnChangePic = findViewById(R.id.btnChangePic);
        Button btnSaveProfile = findViewById(R.id.btnSaveProfile);
        Button btnResetProfile = findViewById(R.id.btnResetProfile);
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etDOB = findViewById(R.id.etDOB);

        // Initialize SharedPreferences for user profile data
        sharedPreferences = getSharedPreferences("UserProfile", MODE_PRIVATE);

        // Load existing user information when the activity starts
        loadUserInfo();

        // Set up click listener for the "Change Picture" button
        btnChangePic.setOnClickListener(v -> openImageChooser());

        // Set up click listener for the "Date of Birth" EditText to show a DatePicker
        etDOB.setOnClickListener(v -> showDatePicker());

        // Set up click listener for the "Save Profile" button
        btnSaveProfile.setOnClickListener(v -> {
            if (validateInput()) { // Validate input fields before saving
                saveUserInfo();
            }
        });

        // Set up click listener for the "Reset Profile" button
        btnResetProfile.setOnClickListener(v -> resetUserInfo());
    }

    /**
     * Opens an image chooser intent to allow the user to select a profile picture.
     */
    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*"); // Specifies that we want image files
        intent.setAction(Intent.ACTION_GET_CONTENT); // Allows selecting content from various sources
        startActivityForResult(Intent.createChooser(intent, "Select Profile Picture"), PICK_IMAGE_REQUEST);
    }

    /**
     * Handles the result from other activities, specifically the image chooser.
     *
     * @param requestCode The integer request code originally supplied to startActivityForResult(),
     * allowing you to identify who this result came from.
     * @param resultCode  The integer result code returned by the child activity through its setResult().
     * @param data        An Intent, which can return result data to the caller (various data can be attached to Intent "extras").
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check if the result is for our image pick request and was successful
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData(); // Get the URI of the selected image
            try {
                // Convert the URI to a Bitmap and set it to the ImageView
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                imgProfilePic.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Displays a DatePickerDialog for the user to select their date of birth.
     */
    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance(); // Get current date for default selection

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, month1, dayOfMonth) -> {
                    // Format the selected date and set it to the EditText
                    String date = (dayOfMonth < 10 ? "0" + dayOfMonth : dayOfMonth) + "/"
                            + ((month1 + 1) < 10 ? "0" + (month1 + 1) : (month1 + 1)) + "/" + year1;
                    etDOB.setText(date);
                }, year, month, day);

        datePickerDialog.show();
    }

    /**
     * Validates the input fields to ensure they are not empty.
     *
     * @return true if all required fields are filled, false otherwise.
     */
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

    /**
     * Saves the user's profile information to SharedPreferences.
     */
    private void saveUserInfo() {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Store text fields
        editor.putString("name", etName.getText().toString());
        editor.putString("email", etEmail.getText().toString());
        editor.putString("phone", etPhone.getText().toString());
        editor.putString("dob", etDOB.getText().toString());

        // Store the URI of the profile picture if available
        if (imageUri != null) {
            editor.putString("profilePicUri", imageUri.toString());
        }

        editor.apply(); // Apply changes asynchronously

        Toast.makeText(this, "Profile saved successfully", Toast.LENGTH_SHORT).show();
    }

    /**
     * Loads the user's profile information from SharedPreferences and populates the UI.
     */
    private void loadUserInfo() {
        // Retrieve and set text field values
        etName.setText(sharedPreferences.getString("name", ""));
        etEmail.setText(sharedPreferences.getString("email", ""));
        etPhone.setText(sharedPreferences.getString("phone", ""));
        etDOB.setText(sharedPreferences.getString("dob", ""));

        // Retrieve and set profile picture
        String profilePicString = sharedPreferences.getString("profilePicUri", null);
        if (profilePicString != null) {
            imageUri = Uri.parse(profilePicString); // Convert stored string back to URI
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                imgProfilePic.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
                // If there's an error loading the saved image, revert to default
                imgProfilePic.setImageResource(R.drawable.ic_baseline_person_24);
            }
        }
    }

    /**
     * Resets all input fields and the profile picture to their default states.
     */
    private void resetUserInfo() {
        etName.setText("");
        etEmail.setText("");
        etPhone.setText("");
        etDOB.setText("");
        imgProfilePic.setImageResource(R.drawable.ic_baseline_person_24); // Set default profile icon
        imageUri = null; // Clear the stored image URI
        // Optionally, you might want to clear the SharedPreferences data as well for a full reset:
        Toast.makeText(this, "Profile fields reset.", Toast.LENGTH_SHORT).show();
    }
}