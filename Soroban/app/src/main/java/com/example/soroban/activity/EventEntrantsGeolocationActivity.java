package com.example.soroban.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import com.example.soroban.FireBaseController;
import com.example.soroban.R;
import com.example.soroban.model.Event;
import com.example.soroban.model.User;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.List;

/*References:
/ https://help.famoco.com/developers/media/display-map/
/ https://mrappbuilder.medium.com/how-to-integrate-and-work-with-open-street-map-osm-in-an-android-app-kotlin-564b38590bfe
/ https://github.com/johnjohndoe/OSMDroidOfflineDemo/blob/master/app/src/main/java/com/example/android/osmdroidofflinedemo/MainActivity.java
/ https://sachankapil.medium.com/latest-method-how-to-get-current-location-latitude-and-longitude-in-android-give-support-for-c5132474c864
/ https://stackoverflow.com/questions/6694391/get-current-location-of-user-in-android-without-using-gps-or-internet
*/
/**
 * Displays the geolocation of entrants for a selected event
 * Allows organizers to view and interact with user locations on a map.
 * @author Matthieu Larochelle
 * @see Event
 * @see User
 * @see FireBaseController
 * @see <a href="https://help.famoco.com/developers/media/display-map/">Famoco Map Display Guide</a>
 * @see <a href="https://github.com/johnjohndoe/OSMDroidOfflineDemo">OSMDroid Demo</a>
 */
public class EventEntrantsGeolocationActivity extends AppCompatActivity{
    private Event selectedEvent;
    private User appUser;
    private ActivityResultLauncher<String> requestPersmissionLauncher;
    private LocationManager locationManager;
    private Location userLocation = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context context = getApplicationContext();
        //important! set your user agent to prevent getting banned from the osm servers
        Configuration.getInstance().load(context , PreferenceManager.getDefaultSharedPreferences(context ));
        setContentView(R.layout.organizer_event_entrants_geolocation);

        Bundle args = getIntent().getExtras();

        // Initialize appUser and selected Event for this activity.
        if(args != null){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                selectedEvent = args.getSerializable("selectedEvent", Event.class);
                appUser = args.getSerializable("appUser", User.class);
            }else{
                appUser = (User) args.getSerializable("appUser");
                selectedEvent = (Event) args.getSerializable("selectedEvent");
            }

            if(appUser == null || selectedEvent == null){
                throw new IllegalArgumentException("Must pass object of type User and Event to initialize appUser.");
            }else{
                selectedEvent = appUser.getHostedEvents().find(selectedEvent);
            }

        }else{
            throw new IllegalArgumentException("Must pass arguments to initialize this activity.");
        }


        ProgressBar progress = findViewById(R.id.geo_progress_bar);

        //Set-up map
        MapView map = (MapView) findViewById(R.id.map_view);
        map.setHorizontalMapRepetitionEnabled(false);
        map.setVerticalMapRepetitionEnabled(false);
        map.setVisibility(View.VISIBLE);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);
        IMapController mapController = map.getController();
        mapController.setZoom(9.0);
        progress.setVisibility(View.GONE);

        for(int i = 0; i < selectedEvent.getWaitingEntrants().size(); i++){
            User entrant = selectedEvent.getWaitingEntrants().get(i);
            GeoPoint userPoint = entrant.getLocation();

            if(userPoint == null){
                Toast.makeText(this, "Error retrieving entrant geolocation. Some entrants may not be displayed.", Toast.LENGTH_SHORT).show();
            }

            if (userPoint != null){
                Marker newMarker = new Marker(map);
                newMarker.setPosition(userPoint);
                newMarker.setIcon(ContextCompat.getDrawable(getApplicationContext(),org.osmdroid.library.R.drawable.ic_menu_mylocation));
                newMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                newMarker.setTitle(entrant.getFirstName() + " " + entrant.getLastName());

                map.getOverlays().add(newMarker);
            }
        }

        // Check if location permissions are on
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        requestPersmissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted ->{
            if(isGranted){
                // App can access geo location
            }else{
                // App cant access geo location
            }
        });
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                // App can use location
                List<String> providers = locationManager.getProviders(true);
                for(String provider : providers){
                    userLocation = locationManager.getLastKnownLocation(provider);
                    if(userLocation != null){
                        GeoPoint startGeo = new GeoPoint(userLocation.getLatitude(), userLocation.getLongitude());
                        IGeoPoint start = startGeo;
                        mapController.setCenter(start);
                    }
                }

        }else if(shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION) && shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)){
                // Prompt user to accept or decline location permissions
        }else {
                // Directly ask for permission
                requestPersmissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION);
                requestPersmissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }

    }

}