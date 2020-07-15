package com.example.buckos.login_registration;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.buckos.R;
import com.example.buckos.main_screen.MainActivity;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

// This activity logs user in and maintain persistence
public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
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

    // On click listener for login button
    public void onLogInClicked(View view) {
        String username = mUsernameEt.getText().toString();
        String password = mPasswordEt.getText().toString();
        loginUser(username, password);
    }

    // On click listener for Create an account, direct user to Sign up screen
    public void onCreateAccountClicked(View view) {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }

    // Function that authenticates a user based on input username and password
    private void loginUser(String username, String password) {
        // user get logged in with the provided username and password
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            public void done(ParseUser user, ParseException e) {
                if (user == null) {
                    // Log in failed.
                    Toast.makeText(LoginActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }
                // User is logged in successfully, navigate to Home/Feed.
                startMainActivity();
                Toast.makeText(LoginActivity.this, "Welcome to Buck It!", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish(); // prevents user from going back to login screen
    }

}