package com.example.soroban;

import android.net.Uri;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ProfileRepository {
    public void uploadImageToFirebase(Uri imageUri, String userId) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference("profile_pictures/" + userId + ".jpg");
        storageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
            storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                saveImageUriToDatabase(uri.toString(), userId);
            });
        }).addOnFailureListener(e -> {
            // Handle failure
        });
    }

    private void saveImageUriToDatabase(String uri, String userId) {
        FirebaseFirestore.getInstance().collection("users").document(userId)
                .update("profileImageUrl", uri);
    }

    public void deleteProfileImage(String userId) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference("profile_pictures/" + userId + ".jpg");
        storageRef.delete().addOnSuccessListener(aVoid -> {
            removeImageUriFromDatabase(userId);
        }).addOnFailureListener(e -> {
            // Handle failure
        });
    }

    private void removeImageUriFromDatabase(String userId) {
        FirebaseFirestore.getInstance().collection("users").document(userId)
                .update("profileImageUrl", FieldValue.delete());
    }
}
