package com.example.soroban.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.soroban.FireBaseController;
import com.example.soroban.R;
import com.example.soroban.activity.UserDashboardActivity;
import com.example.soroban.model.User;

public class CreateAccountActivity extends AppCompatActivity {

    private EditText etFirstName, etLastName, etEmail, etPhoneNumber;
    private Button btnCreateAccount;
    private ProgressBar progressBar;

    private FireBaseController firebaseController;
    private String deviceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_account);

        // Initialize views
        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etEmail = findViewById(R.id.etEmail);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        btnCreateAccount = findViewById(R.id.btnCreateAccount);
        progressBar = findViewById(R.id.progressBar);

        // Initialize Firebase Controller
        firebaseController = new FireBaseController(this);

        // Get the device ID passed from MainActivity
        deviceId = getIntent().getStringExtra("deviceId");

        // Set button click listener
        btnCreateAccount.setOnClickListener(v -> createAccount());
    }

    /**
     * Validates user input and saves the new user to Firebase using FireBaseController.
     */
    private void createAccount() {
        // Get input values
        String firstName = etFirstName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phoneNumber = etPhoneNumber.getText().toString().trim();

        // Validate inputs
        if (TextUtils.isEmpty(firstName)) {
            etFirstName.setError("First name is required");
            etFirstName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(lastName)) {
            etLastName.setError("Last name is required");
            etLastName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email) || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Enter a valid email address");
            etEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(phoneNumber) || phoneNumber.length() < 10) {
            etPhoneNumber.setError("Enter a valid phone number");
            etPhoneNumber.requestFocus();
            return;
        }

        // Show progress bar
        progressBar.setVisibility(ProgressBar.VISIBLE);

        // Create User object
        User newUser = new User(deviceId);


        // Save user to Firebase using FireBaseController
        firebaseController.createUserDb(newUser);

        // Redirect to User Dashboard
        Toast.makeText(CreateAccountActivity.this, "Account created successfully!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(CreateAccountActivity.this, UserDashboardActivity.class);
        intent.putExtra("appUser", newUser);
        startActivity(intent);
        finish(); // Close CreateAccountActivity
    }
}
