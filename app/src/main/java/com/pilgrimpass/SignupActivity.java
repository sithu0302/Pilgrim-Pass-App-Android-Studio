package com.pilgrimpass;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SignupActivity extends AppCompatActivity {

    private String selectedUserType = "", selectedProvince = "";
    private EditText editUsername, editPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        Spinner spinnerUserType = findViewById(R.id.spinnerUserType);
        Spinner spinnerProvince = findViewById(R.id.spinnerProvince);
        editUsername = findViewById(R.id.editUsername);
        editPassword = findViewById(R.id.editPassword);
        Button btnSignup = findViewById(R.id.Signup);

        // Spinner setup - User Type
        ArrayAdapter<String> userTypeAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                new String[]{"Select user type", "Admin", "Public"}
        );
        userTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUserType.setAdapter(userTypeAdapter);

        spinnerUserType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedUserType = parent.getItemAtPosition(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Spinner setup - Province
        ArrayAdapter<String> provinceAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                new String[]{
                        "Select province",
                        "Western Province", "Central Province", "Southern Province",
                        "Eastern Province", "Northern Province", "North Western Province",
                        "North Central Province", "Uva Province", "Sabaragamuwa Province"
                }
        );
        provinceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerProvince.setAdapter(provinceAdapter);

        spinnerProvince.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedProvince = parent.getItemAtPosition(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Signup Button
        btnSignup.setOnClickListener(v -> {

            String username = editUsername.getText().toString().trim();
            String password = editPassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()
                    || selectedUserType.equals("Select user type")
                    || selectedProvince.equals("Select province")) {
                Toast.makeText(this, "Please fill all fields correctly", Toast.LENGTH_SHORT).show();
                return;
            }

            // ✅ Save data to SharedPreferences
            SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("username", username);
            editor.putString("password", password);
            editor.putString("userType", selectedUserType);
            editor.putString("province", selectedProvince);
            editor.apply();

            Toast.makeText(this, "Signup successful!", Toast.LENGTH_SHORT).show();

            // Navigate to Login screen
            startActivity(new Intent(SignupActivity.this, LoginActivity.class));
            finish();
        });
    }
}