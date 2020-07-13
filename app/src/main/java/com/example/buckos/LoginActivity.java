package com.example.buckos;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.parse.ParseUser;

public class LoginActivity extends AppCompatActivity {
    private Button mLogInBtn;
    private EditText mUsernameEt;
    private EditText mPasswordEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Find views
        mLogInBtn = findViewById(R.id.logInBtn);
        mUsernameEt = findViewById(R.id.usernameEt);
        mPasswordEt = findViewById(R.id.passwordEt);

        // If a current is already logged in, we will not ask them to login again
        // Instead, direct them to HomeActivity and skip Login screen.
        if (ParseUser.getCurrentUser() != null) {
            startMainActivity();
        }

        // Find views
        mUsernameEt = findViewById(R.id.usernameEt);
        mPasswordEt = findViewById(R.id.passwordEt);


    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}