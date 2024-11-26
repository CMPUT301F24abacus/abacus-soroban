package com.example.soroban.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.soroban.R;
import com.example.soroban.model.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;

public class ViewProfileFragment extends DialogFragment {
    int defaultPictureID;

    public static ViewProfileFragment newInstance(@Nullable User user) {
        Bundle args = new Bundle();
        args.putSerializable("appUser", user);

        ViewProfileFragment newFragment = new ViewProfileFragment();
        newFragment.setArguments(args);
        return newFragment;
    }

    @SuppressLint({"DialogFragmentCallbacksDetector", "SetTextI18n"})
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.user_profile_view, null);

        // Get references to all views that will display a profile's information.
        TextView firstNameText = view.findViewById(R.id.user_FirstNameField);
        TextView lastNameText = view.findViewById(R.id.user_LastNameField);
        TextView emailText = view.findViewById(R.id.user_emailAddressField);
        TextView phoneNumberText = view.findViewById(R.id.user_phoneNumberField);
        ImageView userProfilePhoto = view.findViewById(R.id.userProfilePhoto);

        Bundle args = getArguments();
        if (args != null) {
            Dialog newDialog = super.onCreateDialog(savedInstanceState);
            newDialog.setContentView(view);
            newDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            Serializable user = args.getSerializable("appUser");

            if (user != null) {
                User appUser = (User) user;

                // Update views to have correct info
                firstNameText.setText(appUser.getFirstName());
                lastNameText.setText(appUser.getLastName());
                emailText.setText(appUser.getEmail());
                phoneNumberText.setText(String.valueOf(appUser.getPhoneNumber()));

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
                                // Get the users first name and set the user's pfp to a default letter
                                // corresponding to their first name's first letter.
                                Log.d("User's first name:", appUser.getFirstName());
                                defaultPictureID = getDefaultPictureID(appUser.getFirstName(), requireContext());
                                userProfilePhoto.setImageResource(defaultPictureID);
                            }
                        })
                        .addOnFailureListener(e -> {
                            Log.e("ViewProfileFragment", "Failed to load image URL", e);
                            userProfilePhoto.setImageResource(R.drawable.ic_profile); // Default image if fetch fails
                        });


                // Set up listeners for buttons
                FloatingActionButton editButton = view.findViewById(R.id.floatingEditButton);
                FloatingActionButton backButton = view.findViewById(R.id.floatingBackButton);

                editButton.setOnClickListener(v -> {
                    ManageProfileFragment dialogFragment = ManageProfileFragment.newInstance(appUser);
                    newDialog.dismiss();
                    dialogFragment.show(getParentFragmentManager(), "Manage profile");
                });

                backButton.setOnClickListener(v -> newDialog.dismiss());

            } else {
                throw new RuntimeException("Must pass User object to populate fields.");
            }
            return newDialog;

        } else {
            throw new RuntimeException("Instantiate ViewProfileFragment by using newInstance() method.");
        }
    }

    /**
     * Getter for getting the defaultPictureID. This is important for displaying the default picture in
     * the edit profile page. This allows ManageProfileFragment to access the attribute so it can display
     * the default picture by ID as well.
     * @return an int that corresponds to the default picture's ID
     */
    public static int getDefaultPictureID(String firstName, Context context) {
        if (firstName == null || firstName.isEmpty()) {
            return R.drawable.ic_profile; // Default fallback image
        }

        char firstLetter = Character.toLowerCase(firstName.charAt(0)); // Get the first letter and ensure it's lowercase
        if (firstLetter < 'a' || firstLetter > 'z') {
            return R.drawable.ic_profile; // Default fallback if the first letter is not A-Z
        }

        // Dynamically fetch the drawable resource
        int drawableId = context.getResources().getIdentifier(
                "letter_" + firstLetter,
                "drawable",
                context.getPackageName()
        );

        return (drawableId != 0) ? drawableId : R.drawable.ic_profile; // Return the default image if the drawable is not found
    }

}
