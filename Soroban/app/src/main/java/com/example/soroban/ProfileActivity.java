package com.example.soroban;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.soroban.ProfileRepository;

public class ProfileActivity extends AppCompatActivity {
    private ProfileRepository profileRepository;
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("ProfileActivity", "onCreate: Activity started"); // Sanity check
        setContentView(R.layout.activity_main);

        profileRepository = new ProfileRepository();

        Log.d("ProfileActivity", "onCreate: Activity created");

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    Log.d("ProfileActivity", "ActivityResult: Received result from image picker");
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        Log.d("ProfileActivity", "ActivityResult: Image URI received - " + imageUri);
                        profileRepository.uploadImageToFirebase(imageUri, "test_user_id");
                    } else {
                        Log.d("ProfileActivity", "ActivityResult: No image selected or result not OK");
                    }
                }
        );

        Button testUploadButton = findViewById(R.id.btn_test_upload);
        testUploadButton.setOnClickListener(v -> {
            Log.d("ProfileActivity", "Test Upload button clicked");
            openImagePicker();
        });

        Button testDeleteButton = findViewById(R.id.btn_test_delete);
        testDeleteButton.setOnClickListener(v -> {
            Log.d("ProfileActivity", "Test Delete button clicked");
            profileRepository.deleteProfileImage("test_user_id");
        });
    }

    private void openImagePicker() {
        Log.d("ProfileActivity", "openImagePicker: Opening image picker");
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

}
