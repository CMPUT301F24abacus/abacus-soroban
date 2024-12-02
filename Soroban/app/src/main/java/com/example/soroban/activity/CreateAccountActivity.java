package com.example.soroban.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.soroban.FireBaseController;
import com.example.soroban.R;
import com.example.soroban.activity.UserDashboardActivity;
import com.example.soroban.model.User;
import com.google.firebase.firestore.DocumentSnapshot;

public class CreateAccountActivity extends AppCompatActivity {

    private static final String TAG = "CreateAccountActivity"; // For logging
    private EditText etFirstName, etLastName, etEmail, etPhoneNumber;
    private Button btnCreateAccount;
    private ProgressBar progressBar;
    private User appUser;

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

        // Get the appUser object
        Bundle args = getIntent().getExtras();

        // Initialize appUser for this activity.
        if (args != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                appUser = args.getSerializable("appUser", User.class);
            } else {
                appUser = (User) args.getSerializable("appUser");
            }
            assert appUser != null;
            deviceId = appUser.getDeviceId();
        }

        if (deviceId == null) {
            Log.e(TAG, "Device ID is null. Cannot proceed.");
            Toast.makeText(this, "Error: Device ID is null.", Toast.LENGTH_SHORT).show();
            finish(); // End the activity if device ID is missing
            return;
        }
        Log.d(TAG, "Device ID: " + deviceId);

        // Set button click listener
        btnCreateAccount.setOnClickListener(v -> {
            try {
                createAccount();
            } catch (Exception e) {
                Log.e(TAG, "Error on create account button click", e);
                Toast.makeText(this, "Unexpected error occurred.", Toast.LENGTH_SHORT).show();
            }
        });
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

        Log.d(TAG, "Creating account with details - First Name: " + firstName + ", Last Name: " + lastName +
                ", Email: " + email + ", Phone Number: " + (TextUtils.isEmpty(phoneNumber) ? "Not Provided" : phoneNumber));

        // Validate inputs
        if (TextUtils.isEmpty(firstName)) {
            etFirstName.setError("First name is required");
            etFirstName.requestFocus();
            progressBar.setVisibility(ProgressBar.GONE);
            return;
        }

        if (TextUtils.isEmpty(lastName)) {
            etLastName.setError("Last name is required");
            etLastName.requestFocus();
            progressBar.setVisibility(ProgressBar.GONE);
            return;
        }

        if (TextUtils.isEmpty(email) || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Enter a valid email address");
            etEmail.requestFocus();
            progressBar.setVisibility(ProgressBar.GONE);
            return;
        }
        try {
            // Create User object
            User newUser;
            if (TextUtils.isEmpty(phoneNumber)) {
                // If phone number is not provided
                newUser = new User(deviceId);
                newUser.setFirstName(firstName);
                newUser.setLastName(lastName);
                newUser.setEmail(email);
                newUser.setPhoneNumber(0); // Use 0 or a default value for phoneNumber
            } else {
                // If phone number is provided
                newUser = new User(deviceId);
                newUser.setFirstName(firstName);
                newUser.setLastName(lastName);
                newUser.setEmail(email);
                newUser.setPhoneNumber(Long.parseLong(phoneNumber));
            }
            newUser.setAdminCheck(false);

            // Save user to Firebase using FireBaseController
            progressBar.setVisibility(View.VISIBLE);
            firebaseController.createUserDb(newUser);
            firebaseController.initialize(progressBar, newUser, btnCreateAccount);
            btnCreateAccount.setVisibility(View.GONE);
            Log.d(TAG, "Account created successfully. Redirecting to dashboard.");
            Toast.makeText(CreateAccountActivity.this, "Account created successfully!", Toast.LENGTH_SHORT).show();
            redirectToDashboard(newUser);
        } catch (Exception e) {
            Log.e(TAG, "Error creating user account", e);
            progressBar.setVisibility(ProgressBar.GONE);
            Toast.makeText(this, "Failed to create account. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Redirects the user to the dashboard activity.
     *
     * @param user The user object to pass to the dashboard.
     */
    private void redirectToDashboard(User user) {
        if (user == null) {
            Log.e(TAG, "User object is null. Cannot redirect to dashboard.");
            Toast.makeText(this, "Error: User data is invalid.", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(CreateAccountActivity.this, UserDashboardActivity.class);
        intent.putExtra("appUser", user);
        startActivity(intent);
        finish();
    }
}