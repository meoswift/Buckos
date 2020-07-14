package com.example.buckos.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.buckos.R;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

// This activity allows user to sign up with a new account
public class SignUpActivity extends AppCompatActivity {

    private EditText mUsernameEt;
    private EditText mPasswordEt;
    private EditText mDisplayNameEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Find views
        mUsernameEt = findViewById(R.id.usernameEt);
        mPasswordEt = findViewById(R.id.passwordEt);
        mDisplayNameEt = findViewById(R.id.displayNameEt);

    }

    // When user click sign up, create a new user in database
    public void onSignUpClicked(View view) {
        String username = mUsernameEt.getText().toString();
        String password = mPasswordEt.getText().toString();
        String displayName = mDisplayNameEt.getText().toString();
        createAccount(username, password, displayName);
    }

    // Create new account using given credentials
    private void createAccount(String username, String password, String displayName) {
        // Create the ParseUser
        ParseUser user = new ParseUser();
        // Set core properties
        user.setUsername(username);
        user.setPassword(password);
        user.put("name", displayName);

        // Invoke signUpInBackground
        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e != null) {
                    // Log in failed. Check logcat for error and send a Toast to let user know
                    return;
                }
                // User is logged in successfully, navigate to Home/Feed.
                Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                startActivity(intent);
                finish(); // prevents user from going back to login screen
            }
        });
    }
}