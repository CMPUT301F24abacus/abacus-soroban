package com.example.soroban.activity;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.soroban.FireBaseController;
import com.example.soroban.R;
import com.example.soroban.model.Event;
import com.example.soroban.model.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.FirebaseDatabase;

public class AdminViewProfileActivity extends AppCompatActivity {
    private User selectedUser;
    private User appUser;
    private TextView userFirstName;
    private TextView userLastName;
    private TextView userEmail;
    private TextView userPhoneNumber;
    private Button deleteProfile;
    private FloatingActionButton button1;
    private FloatingActionButton button2;
    private ImageView userProfilePic;
    private FireBaseController firebaseController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile_view);

        Bundle args = getIntent().getExtras();

        // Initialize appUser and selected Event for this activity.
        if(args != null){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                selectedUser = args.getSerializable("selectedUser", User.class);
                appUser = args.getSerializable("appUser", User.class);
            }else{
                appUser = (User) args.getSerializable("appUser");
                selectedUser = (User) args.getSerializable("selectedUser");
            }

            if(appUser == null || selectedUser == null){
                throw new IllegalArgumentException("Must pass object of type User to initialize appUser.");
            }

        }else{
            throw new IllegalArgumentException("Must pass arguments to initialize this activity.");
        }

        firebaseController = new FireBaseController(this);

        // Initialize buttons, etc.
        button1 = findViewById(R.id.floatingEditButton);
        button1.setVisibility(View.INVISIBLE);
        button2 = findViewById(R.id.floatingBackButton);
        button2.setVisibility(View.INVISIBLE);
        deleteProfile = findViewById(R.id.delete_profile_btn);
        deleteProfile.setVisibility(View.VISIBLE);
        userFirstName = findViewById(R.id.user_FirstNameField);
        userLastName = findViewById(R.id.user_LastNameField);
        userEmail = findViewById(R.id.user_emailAddressField);
        userPhoneNumber = findViewById(R.id.user_phoneNumberField);
        userProfilePic = findViewById(R.id.userProfilePhoto);

        userFirstName.setText(selectedUser.getFirstName());
        userLastName.setText(selectedUser.getLastName());
        userEmail.setText(selectedUser.getEmail());
        userPhoneNumber.setText(String.valueOf(selectedUser.getPhoneNumber()));
        // Fetch and display the profile image
        FirebaseDatabase.getInstance().getReference("users")
                .child(selectedUser.getDeviceId())
                .child("profileImageUrl")
                .get()
                .addOnSuccessListener(snapshot -> {
                    String imageUrl = snapshot.getValue(String.class);
                    Log.d("ViewProfileFragment", "Fetched image URL: " + imageUrl);  // Logging URL for debugging
                    if (imageUrl != null) {
                        Log.e("ViewProfileFragment", "before glide");
                        Glide.with(this)
                                .load(imageUrl)
                                .diskCacheStrategy(DiskCacheStrategy.NONE)  // Disable caching to ensure updated image
                                .skipMemoryCache(true)
                                .into(userProfilePic);
                        Log.e("ViewProfileFragment", "After glide");
                    } else {
                        userProfilePic.setImageResource(R.drawable.ic_profile); // Set default if no URL
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("ViewProfileFragment", "Failed to load image URL", e);
                    userProfilePic.setImageResource(R.drawable.ic_profile); // Default image if fetch fails
                });




    }
}