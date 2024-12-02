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
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.example.soroban.FireBaseController;
import com.example.soroban.model.User;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.util.GeoPoint;

import java.util.List;

/**
 * Displays a confirmation dialog for users to allow location access for joining an event's waitlist.
 * Integrates location permissions and services to retrieve the user's geolocation.
 *
 * @author
 * @see User
 * @see GeolocationListener
 * @see FireBaseController
 */
public class ConfirmGiveLocationFragment  extends DialogFragment {
    private User appUser;
    private FireBaseController fireBaseController;
    private GeolocationListener listener;
    private ActivityResultLauncher<String> requestPermissionLauncher;
    private LocationManager locationManager;
    private Location location = null;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if(context instanceof GeolocationListener){
            listener = (GeolocationListener) context;
        }else{
            throw new RuntimeException(context + "must implement GeolocationListener");
        }

        requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted ->{
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
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("This event requires your location to join the waitlist. Give location?");
        builder.setNegativeButton("No", (dialog, which) -> {
            listener.returnResult(false);
        });
        builder.setPositiveButton("Yes", (dialog, which) -> {
            // Check if location permissions are on
            locationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);

            if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                // App can use location
                List<String> providers = locationManager.getProviders(true);
                for(String provider : providers){
                    location = locationManager.getLastKnownLocation(provider);
                    if(location != null){
                        listener.setLocation(location.getLatitude(), location.getLongitude());
                    }
                }

            }else if(shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION) && shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)){
                // Prompt user to accept or decline location permissions
            }else {
                // Directly ask for permission
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION);
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
            }

            listener.returnResult(true);
        });

        return builder.create();
    }
}
