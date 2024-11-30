package com.example.soroban.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.soroban.FireBaseController;
import com.example.soroban.databinding.ActivityScanQrCodeBinding;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import com.example.soroban.model.Event;
import com.example.soroban.model.User;

/**
 * Handles the functionality for scanning QR codes
 * and redirects the user to EventDetailsActivity with the scanned event data.
 * References: YouTube, StackOverflow
 *
 * @author: Edwin Manalastas
 * @see Event
 * @see User
 * @see EventDetailsActivity
 * @see FireBaseController
 */
public class QrCodeScanActivity extends AppCompatActivity {
    private User appUser;
    private boolean isRegistered; //
    private ActivityScanQrCodeBinding binding;

    /**
     * Called when the activity is created.
     * Initializes binding, retrieves appUser, and sets up views.
     *
     * @param savedInstanceState The saved state of the activity.
     * @throws IllegalArgumentException if the required appUser argument is not provided.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initBinding();

        // Retrieve appUser from intent
        Bundle args = getIntent().getExtras();
        if (args != null) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                appUser = args.getSerializable("appUser", User.class);
            } else {
                appUser = (User) args.getSerializable("appUser");
            }

            if (appUser == null) {
                throw new IllegalArgumentException("Must pass object of type User to initialize appUser.");
            }
        } else {
            throw new IllegalArgumentException("Must pass arguments to initialize this activity.");
        }

        initViews();
    }

    /**
     * Launcher to handle camera permission requests.
     */
    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if(isGranted) {
                    showCamera();
                }
                else {
                    Toast.makeText(this, "Camera permission is required to scan QR codes", Toast.LENGTH_SHORT).show();
                }
            });

    /**
     * Launcher to handle QR code scanning results.
     */
    private ActivityResultLauncher<ScanOptions> qrCodeLauncher = registerForActivityResult(new ScanContract(), result -> {
        if (result.getContents() == null) {
            Toast.makeText(this, "Scan Cancelled", Toast.LENGTH_SHORT).show();
        } else {
            retrieveEventDetails(result.getContents());
        }
    });

    /**
     * Processes the scanned QR code hash to fetch event details.
     *
     * @param qrCodeHash The hash value scanned from the QR code.
     */
    // Action after scanning QR Code
    private void retrieveEventDetails(String qrCodeHash) {

        // check to see if hash is scanned properly
        Log.d("QRCodeScan", "Scanned QR Code Hash: " + qrCodeHash);

        FireBaseController dbController = new FireBaseController(this);
        dbController.fetchEventByQRCodeHash(qrCodeHash, event -> {
            if (event != null) {
                // Start EventDetailsActivity with event data
                Intent intent = new Intent(QrCodeScanActivity.this, EventDetailsActivity.class);
                intent.putExtra("eventData", event);
                intent.putExtra("appUser", appUser); // Pass the appUser object
                startActivity(intent);
            } else {
                Toast.makeText(this, "Event not found", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Launches the camera for scanning QR codes.
     */
    private void showCamera() {
        ScanOptions options = new ScanOptions();
        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
        options.setPrompt("Scan QR code");
        options.setCameraId(0); // Use back camera
        options.setBeepEnabled(false);
        options.setBarcodeImageEnabled(true);
        options.setOrientationLocked(false);

        qrCodeLauncher.launch(options);
    }

    /**
     * Initializes views and sets up the FloatingActionButton for scanning QR codes.
     */
    private void initViews() {
        binding.fab.setOnClickListener(view -> {
            checkPermissionAndShowActivity(this);
        });
    }

    /**
     * Checks for camera permission and handles user redirection to app settings if necessary.
     *
     * @param context The context for checking permissions.
     */
    private void checkPermissionAndShowActivity(Context context) {
        if(ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED) {
            showCamera();
        } else if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
            Toast.makeText(this, "Camera permission required", Toast.LENGTH_SHORT).show();

            // If permission is denied, show a dialog that directs user to app settings
            new AlertDialog.Builder(this)
                    .setTitle("Permission Required")
                    .setMessage("Camera permission is required to scan QR codes. Please enable it in the app settings.")
                    .setPositiveButton("Go to Settings", (dialog, which) -> {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                    .show();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    /**
     * Initializes view binding for the activity.
     */
    private void initBinding() {
        binding = ActivityScanQrCodeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

    }
}