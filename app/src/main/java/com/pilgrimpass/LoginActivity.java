package com.pilgrimpass;


import android.content.Intent;

import android.os.Bundle;

import android.widget.Button;


import androidx.appcompat.app.AppCompatActivity;


public class LoginActivity extends AppCompatActivity {

    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login); // login UI with Login & Signup buttons


        Button btnLogin = findViewById(R.id.Login);

        Button btnGoToSignup = findViewById(R.id.Signup);


// Go to Login1Activity to actually validate login

        btnLogin.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, LoginInsideActivity.class)));


        btnGoToSignup.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, SignupActivity.class)));

    }

} 