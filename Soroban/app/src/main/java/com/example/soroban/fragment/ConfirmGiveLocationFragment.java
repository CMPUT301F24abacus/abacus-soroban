package com.example.soroban.fragment;

import static androidx.core.content.ContextCompat.getSystemService;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.example.soroban.FireBaseController;
import com.example.soroban.R;
import com.example.soroban.model.Event;
import com.example.soroban.model.User;

import org.osmdroid.util.GeoPoint;


public class ConfirmGiveLocationFragment  extends DialogFragment {
    private User appUser;
    private FireBaseController fireBaseController;
    private GeolocationListener listener;
    private ActivityResultLauncher<String> requestPersmissionLauncher;
    private LocationManager locationManager;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if(context instanceof GeolocationListener){
            listener = (GeolocationListener) context;
        }else{
            throw new RuntimeException(context + "must implement GeolocationListener");
        }

        requestPersmissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted ->{
            if(isGranted){
                // App can access geo location
            }else{
                // App cant access geo location
            }
        });
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.user_accept_invitation, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("This event requires your location to join the waitlist. Give location?");
        builder.setView(view);
        builder.setNegativeButton("No", (dialog, which) -> {
            listener.returnResult(false);
        });
        builder.setPositiveButton("Yes", (dialog, which) -> {
            // Check if location permissions are on
            locationManager = (LocationManager) getSystemService(getContext(), LocationManager.class);

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    // App can use location
                    // Check if GPS is available
                    boolean hasGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

                    LocationListener gpsListener = new LocationListener() {
                        @Override
                        public void onLocationChanged(@NonNull Location location) {
                            // Return User's location
                            listener.setLocation(location.getLatitude(), location.getLongitude());
                        }
                    };

                    if (hasGPS) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                5000,
                                0F,
                                gpsListener
                        );
                    }

                } else if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION) && shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    // Prompt user to accept or decline location permissions
                } else {
                    // Directly ask for permission
                    requestPersmissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION);
                    requestPersmissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
                }
            }

            listener.returnResult(true);
        });

        return builder.create();
    }
}
