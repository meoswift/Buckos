package com.example.buckos.ui.authentication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.buckos.R;
import com.example.buckos.ui.MainActivity;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

// This activity logs user in and maintain persistence
public class LoginActivity extends AppCompatActivity {
    private EditText mUsernameEditText;
    private EditText mPasswordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Find views
        mUsernameEditText = findViewById(R.id.usernameEt);
        mPasswordEditText = findViewById(R.id.passwordEt);

        // If a current is already logged in, we will not ask them to login again
        // Instead, direct them to HomeActivity and skip Login screen.
        if (ParseUser.getCurrentUser() != null) {
            startMainActivity();
        }

        // Find views
        mUsernameEditText = findViewById(R.id.usernameEt);
        mPasswordEditText = findViewById(R.id.passwordEt);
    }

    // On click listener for login button
    public void onLogInClicked(View view) {
        String username = mUsernameEditText.getText().toString();
        String password = mPasswordEditText.getText().toString();
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
        ParseUser.logInInBackground(username, password, (user, e) -> {
            if (user == null) {
                // Log in failed.
                Toast.makeText(LoginActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                return;
            }
            // User is logged in successfully, navigate to Home/Feed.
            startMainActivity();
            Toast.makeText(LoginActivity.this, "Login Successfully!", Toast.LENGTH_SHORT).show();
        });
    }


    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish(); // prevents user from going back to login screen
    }

    @Override
    public void onBackPressed() { };
}