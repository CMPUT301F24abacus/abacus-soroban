package com.example.soroban;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.util.Log;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;

public class ManageProfileFragment extends DialogFragment {
    private UserController userController;
    private ProfileRepository profileRepository;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private ImageView userProfilePhoto;
    private User appUser;

    public static ManageProfileFragment newInstance(@NonNull User user){
        Bundle args = new Bundle();
        args.putSerializable("appUser", user);

        ManageProfileFragment newFragment = new ManageProfileFragment();
        newFragment.setArguments(args);
        return newFragment;
    }

    @SuppressLint({"DialogFragmentCallbacksDetector", "SetTextI18n"})
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.user_profile_edit, null);

        // Initialize ProfileRepository and ImageView for profile photo
        profileRepository = new ProfileRepository();
        userProfilePhoto = view.findViewById(R.id.userProfilePhoto);

        // Initialize the image picker launcher
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        Log.d("ManageProfileFragment", "Image URI received: " + imageUri);
                        userProfilePhoto.setImageURI(imageUri); // Display the selected image
                        profileRepository.uploadImageToFirebase(imageUri, appUser.getDeviceId()); // Use actual user ID here
                    } else {
                        Log.d("ManageProfileFragment", "No image selected or result not OK");
                    }
                }
        );

        // Get references to all views that will display a profile's information.
        TextView firstNameEdit = view.findViewById(R.id.user_firstNameEdit);
        TextView lastNameEdit = view.findViewById(R.id.user_lastNameEdit);
        TextView emailEdit = view.findViewById(R.id.user_emailAddressEdit);
        TextView phoneNumberEdit = view.findViewById(R.id.user_PhoneNumberEdit);

        Bundle args = getArguments();
        if (args != null) {
            Dialog newDialog = super.onCreateDialog(savedInstanceState);
            newDialog.setContentView(view);
            newDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            Serializable user = args.getSerializable("appUser");

            if (user != null) {
                appUser = (User) user; // Store appUser reference here

                // Set up the UserController to manage user updates
                userController = new UserController(appUser);

                // Populate fields with user information

                firstNameEdit.setText(appUser.getFirstName());
                lastNameEdit.setText(appUser.getLastName());
                emailEdit.setText(appUser.getEmail());
                phoneNumberEdit.setText(String.valueOf(appUser.getPhoneNumber()));

                // Fetch and display the profile image
                FirebaseDatabase.getInstance().getReference("users")
                        .child(appUser.getDeviceId())
                        .child("profileImageUrl")
                        .get()
                        .addOnSuccessListener(snapshot -> {
                            String imageUrl = snapshot.getValue(String.class);
                            Log.d("ViewProfileFragment", "Fetched image URL: " + imageUrl);  // Logging URL for debugging
                            if (imageUrl != null) {
                                Log.e("ViewProfileFragment", "before glide");
                                Glide.with(requireContext())
                                        .load(imageUrl)
                                        .diskCacheStrategy(DiskCacheStrategy.NONE)  // Disable caching to ensure updated image
                                        .skipMemoryCache(true)
                                        .into(userProfilePhoto);
                                Log.e("ViewProfileFragment", "After glide");
                            } else {
                                userProfilePhoto.setImageResource(R.drawable.ic_profile); // Set default if no URL
                            }
                        })
                        .addOnFailureListener(e -> {
                            Log.e("ViewProfileFragment", "Failed to load image URL", e);
                            userProfilePhoto.setImageResource(R.drawable.ic_profile); // Default image if fetch fails
                        });

                // Set up profile picture buttons
                ImageButton editProfileButton = view.findViewById(R.id.editProfilePhotoButton);
                editProfileButton.setOnClickListener(v -> openImagePicker());

                ImageButton removeProfileButton = view.findViewById(R.id.editProfilePhotoButton2);
                removeProfileButton.setOnClickListener(v -> {
                    profileRepository.deleteProfileImage(appUser.getDeviceId()); // Use actual user ID here
                    userProfilePhoto.setImageResource(R.drawable.ic_profile); // Reset to default image
                });

                // Set up discard and confirm buttons
                Button discardButton = view.findViewById(R.id.discardChangesButton);
                discardButton.setOnClickListener(v -> newDialog.dismiss());

                Button confirmButton = view.findViewById(R.id.confirmChangesButton);
                confirmButton.setOnClickListener(v -> {
                    // Update user information through the UserController
                    userController.updateUser(
                            firstNameEdit.getText().toString(),
                            lastNameEdit.getText().toString(),
                            emailEdit.getText().toString(),
                            phoneNumberEdit.getText().toString()
                    );
                    newDialog.dismiss();
                });
            } else {
                throw new RuntimeException("Must pass User object to populate fields.");
            }
            return newDialog;
        } else {
            throw new RuntimeException("Instantiate ManageProfileFragment by using newInstance() method.");
        }
    }

    private void openImagePicker() {
        Log.d("ManageProfileFragment", "Opening image picker");
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }
}
