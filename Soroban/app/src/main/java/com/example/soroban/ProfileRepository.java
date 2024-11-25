package com.example.soroban;

import android.net.Uri;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ProfileRepository {

    public void uploadImageToFirebase(Uri imageUri, String userId) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference("profile_images/" + userId);

        storageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    Log.d("ProfileRepository", "Image uploaded successfully"); // Added log
                    // Get the download URL for the uploaded image
                    storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();
                        Log.d("ProfileRepository", "Download URL: " + imageUrl); // Added log

                        // Save the URL in the Realtime Database
                        FirebaseDatabase.getInstance().getReference("users")
                                .child(userId)
                                .child("profileImageUrl")
                                .setValue(imageUrl)
                                .addOnSuccessListener(aVoid -> {
                                    Log.d("ProfileRepository", "Profile image URL saved to database"); // Added log
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("ProfileRepository", "Failed to save profile image URL", e); // Added log
                                });
                    }).addOnFailureListener(e -> {
                        Log.e("ProfileRepository", "Failed to get download URL", e); // Added log
                    });
                })
                .addOnFailureListener(e -> {
                    Log.e("ProfileRepository", "Failed to upload profile image", e); // Added log
                });
    }

    public void deleteProfileImage(String userId) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference("profile_images/" + userId);

        storageRef.delete().addOnSuccessListener(aVoid -> {
            // Remove the URL from the Realtime Database
            FirebaseDatabase.getInstance().getReference("users")
                    .child(userId)
                    .child("profileImageUrl")
                    .removeValue()
                    .addOnSuccessListener(aVoid1 -> {
                        Log.d("ProfileRepository", "Profile image URL removed from database");
                    })
                    .addOnFailureListener(e -> {
                        Log.e("ProfileRepository", "Failed to remove profile image URL", e);
                    });
        }).addOnFailureListener(e -> {
            Log.e("ProfileRepository", "Failed to delete profile image from storage", e);
        });
    }
}
