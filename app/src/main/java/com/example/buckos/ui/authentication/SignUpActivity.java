package com.example.buckos.ui.authentication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.buckos.R;
import com.example.buckos.ui.MainActivity;
import com.example.buckos.models.User;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

// This activity allows user to sign up with a new account
public class SignUpActivity extends AppCompatActivity {

    private EditText mUsernameEt;
    private EditText mPasswordEt;
    private EditText mDisplayNameEt;
    private EditText mBioEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Find views
        mUsernameEt = findViewById(R.id.usernameEt);
        mPasswordEt = findViewById(R.id.passwordEt);
        mDisplayNameEt = findViewById(R.id.displayNameEt);
        mBioEt = findViewById(R.id.bioEt);
    }

    // When user click sign up, create a new user in database
    public void onSignUpClicked(View view) {
        String username = mUsernameEt.getText().toString();
        String password = mPasswordEt.getText().toString();
        String displayName = mDisplayNameEt.getText().toString();
        String bio = mBioEt.getText().toString();
        createAccount(username, password, displayName, bio);
    }

    // Create new account using given credentials
    private void createAccount(String username, String password, String displayName, String bio) {

        // Create the ParseUser
        User user = ParseObject.create(User.class);
        // Set core properties
        user.setUsername(username);
        user.setPassword(password);
        user.setName(displayName);
        user.setBio(bio);

        // Invoke signUpInBackground
        user.signUpInBackground(e -> {
            if (e != null) {
                // Log in failed. Check logcat for error and send a Toast to let user know
                Log.d("HuH", e.toString());
                return;
            }
            // User is logged in successfully, navigate to Home/Feed.
            Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
            startActivity(intent);
            Toast.makeText(this, "Welcome to Buck It!", Toast.LENGTH_SHORT).show();
            finish(); // prevents user from going back to sign up screen
        });
    }
}