package com.example.soroban;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationManagerCompat;

import com.example.soroban.model.Event;
import com.example.soroban.model.Notification;
import com.example.soroban.model.User;
import com.example.soroban.model.Facility;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * This class handles interactions with Firebase Firestore and Storage.
 * It manages users, events, facilities, and notifications by performing CRUD operations.
 * @Author: Matthieu Larochelle, Kevin Li, Edwin M
 * @Version: 1.2
 */
public class FireBaseController implements Serializable {
    FirebaseFirestore db;
    CollectionReference userRf;
    CollectionReference eventRf;
    CollectionReference facilityRf;
    CollectionReference imageRf;
    Context context;
    private NotificationManagerCompat notifManager;

    public FireBaseController(Context context){
        this.context = context;
        db = FirebaseFirestore.getInstance();
        userRf = db.collection("users");
        eventRf = db.collection("events");
        facilityRf = db.collection("facilities");
        imageRf = db.collection("images");
    }

    /**
     * Intializes the app's data that is retrieved from Firebase.
     * Ensures that the user can't move forward until data has been retrieved.
     * @Author: Matthieu Larochelle, Kevin Li
     * @Version: 1.0
     * @param progressBar: Progress bar which will be made invisible upon data retrieval.
     * @param user: User for which creating is required.
     * @param button: Button that user will select to continue to load into the app,
     *              made visible after data collection
     */
    public void initialize(ProgressBar progressBar, User user, Button button){
        DocumentReference docRef = userRf.document(user.getDeviceId());

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>(){
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task){
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()){
                        Log.d("Firestore", "User document found.");
                        Map<String, Object> userData = document.getData();
                        assert userData != null;
                        user.setEmail((String) userData.get("email"));
                        user.setFirstName((String) userData.get("firstName"));
                        user.setLastName((String) userData.get("lastName"));
                        user.setPhoneNumber((long) userData.get("phoneNumber"));
                        user.setUsername((String) userData.get("username"));

                        if (userData.get("adminCheck") != null) {
                            user.setAdminCheck((Boolean) userData.get("adminCheck"));
                        } else {
                            user.setAdminCheck(false);
                        }

                        DocumentReference facilityDocRef = (DocumentReference) userData.get("facility");
                        if (facilityDocRef != null) {
                            fetchFacilityDoc(user, facilityDocRef);
                        }
                        fetchWaitListDoc(user);
                        fetchRegisteredDoc(user);
                        fetchNotificationDoc(user);
                        fetchHostedDoc(user);
                        fetchInvitedDoc(user);
                    }else{
                        Log.d("Firestore", "User document not found.");
                        createUserDb(user);
                    }
                    progressBar.setVisibility(View.GONE);
                    button.setVisibility(View.VISIBLE);
                }else{
                    Log.d("Firestore", "get failed with ", task.getException());
                }
            }
        });

    }

    /**
     * Create a new User document in Firebase.
     * @Author: Kevin Li
     * @Version: 1.0
     * @param user: User for which creating is required.
     */
    public void createUserDb(User user) {
        Map<String, Object> data = new HashMap<>();
        data.put("deviceId", user.getDeviceId());
        data.put("firstName", user.getFirstName());
        data.put("lastName", user.getLastName());
        data.put("email", user.getEmail());
        data.put("phoneNumber", user.getPhoneNumber());
        data.put("facility", null);
        data.put("username", user.getDeviceId());
        data.put("adminCheck", user.getAdminCheck());
        userRf
                .document(user.getDeviceId())
                .set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Firestore", "DocumentSnapshot successfully written!");
                    }
                });
    }

    /**
     * Create a new Facility document in Firebase.
     * @Author: Matthieu Larochelle, Kevin Li
     * @Version: 1.0
     * @param facility: Facility for which creating is required.
     */
    public void createFacilityDb(Facility facility) {
        DocumentReference userDoc = userRf.document(facility.getOwner().getDeviceId());
        Map<String, Object> data = new HashMap<>();
        data.put("name", facility.getName());
        data.put("owner", userDoc);
        data.put("details", facility.getDetails().toString());
        facilityRf
                .document(facility.getOwner().getDeviceId())
                .set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                      @Override
                      public void onSuccess(Void aVoid) {
                           Log.d("Firestore", "DocumentSnapshot successfully written!");
                        }
                   });
          updateUserFacility(facility.getOwner());
    }
    
    /**
     * Create a new Event document in Firebase.
     * @Author: Kevin Li
     * @Update: By Jerry Pan added a posterUrl field
     * @Version: 1.1
     * @param event: Event for which updating is required.
     */
    public void createEventDb(Event event) {
        Facility eventFacility = event.getFacility();
        User owner = event.getOwner();
        DocumentReference userDoc = userRf.document(event.getOwner().getDeviceId());
        DocumentReference facilityDoc = facilityRf.document(owner.getDeviceId());
        Map<String, Object> data = new HashMap<>();
        data.put("owner", userDoc);
        data.put("facility", facilityDoc);
        data.put("eventName", event.getEventName());
        data.put("eventDetails", event.getEventDetails());
        data.put("eventDate", event.getEventDate());
        data.put("drawDate", event.getDrawDate());
        data.put("sampleSize", event.getSampleSize());
        data.put("maxEntrants", event.getMaxEntrants());
        data.put("geoLocation", event.requiresGeolocation());
        data.put("QRHash", event.getQrCodeHash());
        data.put("posterUrl", event.getPosterUrl());
        eventRf
                .document(event.getEventName() + ", " + owner.getDeviceId())
                .set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Firestore", "DocumentSnapshot successfully written!");
                    }
                });
    }


    /**
     * Fetches a User's document in Firebase. If its does not exist, creates a new one.
     * @Author: Matthieu Larochelle
     * @Version: 1.0
     * @param user: User for which fetching is required.
     */
    public void fetchUserDoc(User user){
        // Reference: https://firebase.google.com/docs/firestore/query-data/get-data#java
        DocumentReference docRef = userRf.document(user.getDeviceId());

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>(){
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task){
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()){
                        Log.d("Firestore", "User document found.");
                        Map<String, Object> userData = document.getData();
                        assert userData != null;
                        user.setEmail((String) userData.get("email"));
                        user.setFirstName((String) userData.get("firstName"));
                        user.setLastName((String) userData.get("lastName"));
                        if (userData.get("phoneNumber") != null) { user.setPhoneNumber((long) userData.get("phoneNumber")); }
                        if(document.getGeoPoint("location") != null){
                            user.setLocation(document.getGeoPoint("location").getLatitude(), document.getGeoPoint("location").getLongitude());
                        }
                        DocumentReference facilityDocRef = (DocumentReference) userData.get("facility");
                        if (facilityDocRef != null) {
                            fetchFacilityDoc(user, facilityDocRef);
                        }
                    }else{
                        Log.d("Firestore", "User document not found.");
                        createUserDb(user);
                    }
                }else{
                    Log.d("Firestore", "get failed with ", task.getException());
                }
            }
        });
    }

    /**
     * Fetches a User's Facility's document in Firebase.
     * @Author: Kevin Li
     * @Version: 1.0
     * @param user: User for which fetching is required.
     * @param facilityDocRef: DocumentReference of the required Facility.
     */
    public void fetchFacilityDoc(User user, DocumentReference facilityDocRef){
        facilityDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>(){
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task){
                if(task.isSuccessful()){
                    DocumentSnapshot documentFacility = task.getResult();
                    if(documentFacility.exists()){
                        Log.d("Firestore", "User facility found.");
                        Map<String, Object> facilityData = documentFacility.getData();
                        assert facilityData != null;
                        user.createFacility();
                        String name = (String) facilityData.get("name");
                        String details = (String) facilityData.get("details");
                        user.getFacility().setName(name);
                        user.getFacility().setDetails(details);
                        Log.d("Firestore", user.getFacility().getName());
                    }else{
                        Log.d("Firestore", "User facility not found.");
                    }
                }else{
                    Log.d("Firestore", "get failed with ", task.getException());
                }
            }
        });
    }

    /**
     * Fetches a User's WaitList collection in Firebase.
     * @Author: Kevin Li
     * @Version: 1.0
     * @param user: User for which fetching is required.
     */
    public void fetchWaitListDoc(User user) {
        CollectionReference waitListRef = userRf.document(user.getDeviceId()).collection("waitList");
        waitListRef
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> eventData = document.getData();
                                String eventName = (String) eventData.get("eventName");
                                Date eventDate = document.getDate("eventDate");
                                Date drawDate = document.getDate("drawDate");
                                Integer sampleSize = ((Long) eventData.get("sampleSize")).intValue();
                                Boolean requireLocation = document.getBoolean("geoLocation") != null ? document.getBoolean("geoLocation") : false;
                                User owner = new User((String) eventData.get("owner"));
                                fetchUserDoc(owner);
                                Facility facility = owner.getFacility();
                                Event event = new Event(owner, facility, eventName, eventDate, drawDate,sampleSize);
                                event.setRequiresGeolocation(requireLocation);
                                if (eventData.get("maxEntrants") != null) {
                                    Integer maxEntrants = ((Long) eventData.get("maxEntrants")).intValue();
                                    event.setMaxEntrants(maxEntrants);
                                }
                                user.addToWaitlist(event);
                            }
                        } else {
                            Log.e("Firestore", "Didn't find eventList!");
                        }
                    }
                });
    }

    /**
     * Fetches a User's RegisteredEvents collection in Firebase.
     * @Author: Kevin Li
     * @Version: 1.0
     * @param user: User for which fetching is required.
     */
    public void fetchRegisteredDoc(User user) {
        CollectionReference RegRef = userRf.document(user.getDeviceId()).collection("registeredEvents");
        RegRef
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> eventData = document.getData();
                                String eventName = (String) eventData.get("eventName");
                                Date eventDate = document.getDate("eventDate");
                                Date drawDate = document.getDate("drawDate");
                                Integer sampleSize = ((Long) eventData.get("sampleSize")).intValue();
                                Boolean requireLocation = document.getBoolean("geoLocation") != null ? document.getBoolean("geoLocation") : false;
                                User owner = new User((String) eventData.get("owner"));
                                fetchUserDoc(owner);
                                Facility facility = owner.getFacility();
                                Event event = new Event(owner, facility, eventName, eventDate, drawDate,sampleSize);
                                event.setRequiresGeolocation(requireLocation);
                                if (eventData.get("maxEntrants") != null) {
                                    Integer maxEntrants = ((Long) eventData.get("maxEntrants")).intValue();
                                    event.setMaxEntrants(maxEntrants);
                                }
                                fetchEventWaitlistDoc(event);
                                fetchEventCancelledDoc(event);
                                fetchEventInvitedDoc(event);
                                user.addRegisteredEvent(event);}
                        } else {
                            Log.e("Firestore", "Didn't find eventList!");
                        }
                    }
                });
    }

    /**
     * Fetches a User's InvitedEvents collection in Firebase.
     * @Author: Kevin Li
     * @Version: 1.0
     * @param user: User for which fetching is required.
     */
    public void fetchInvitedDoc(User user) {
        CollectionReference RegRef = userRf.document(user.getDeviceId()).collection("invitedEvents");
        RegRef
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> eventData = document.getData();
                                String eventName = (String) eventData.get("eventName");
                                Date eventDate = document.getDate("eventDate");
                                Date drawDate = document.getDate("drawDate");
                                Integer sampleSize = ((Long) eventData.get("sampleSize")).intValue();
                                Boolean requireLocation = document.getBoolean("geoLocation") != null ? document.getBoolean("geoLocation") : false;
                                User owner = new User((String) eventData.get("owner"));
                                fetchUserDoc(owner);
                                Facility facility = owner.getFacility();
                                Event event = new Event(owner, facility, eventName, eventDate, drawDate,sampleSize);
                                event.setRequiresGeolocation(requireLocation);
                                if (eventData.get("maxEntrants") != null) {
                                    Integer maxEntrants = ((Long) eventData.get("maxEntrants")).intValue();
                                    event.setMaxEntrants(maxEntrants);
                                }
                                fetchEventWaitlistDoc(event);
                                fetchEventCancelledDoc(event);
                                fetchEventAttendeeDoc(event);
                                user.addInvitedEvent(event);
                                }
                        } else {
                            Log.e("Firestore", "Didn't find eventList!");
                        }
                    }
                });
    }

    /**
     * Fetches a User's HostedEvents collection in Firebase.
     * @Author: Matthieu Larochelle, Kevin Li
     * @Version: 1.0
     * @param user: User for which fetching is required.
     */
    public void fetchHostedDoc(User user) {
        CollectionReference RegRef = userRf.document(user.getDeviceId()).collection("hostedEvents");
        RegRef
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> eventData = document.getData();
                                String eventName = (String) eventData.get("eventName");
                                Date eventDate = document.getDate("eventDate");
                                Date drawDate = document.getDate("drawDate");
                                Integer sampleSize = ((Long) eventData.get("sampleSize")).intValue();
                                Boolean requireLocation = document.getBoolean("geoLocation") != null ? document.getBoolean("geoLocation") : false;
                                Facility facility = user.getFacility();
                                Event event = new Event(user, facility, eventName, eventDate, drawDate,sampleSize);
                                event.setRequiresGeolocation(requireLocation);
                                if (eventData.get("maxEntrants") != null) {
                                    Integer maxEntrants = ((Long) eventData.get("maxEntrants")).intValue();
                                    event.setMaxEntrants(maxEntrants);
                                }
                                fetchEventWaitlistDoc(event);
                                fetchEventCancelledDoc(event);
                                fetchEventInvitedDoc(event);
                                fetchEventAttendeeDoc(event);
                                user.addHostedEvent(event);
                            }
                        } else {
                            Log.e("Firestore", "Didn't find eventList!");
                        }
                    }
                });
    }


    /**
     * Fetches a User's notifications collection in Firebase.
     * @Author: Matthieu Larochelle, Kevin Li
     * @Version: 1.0
     * @param user: User for which fetching is required.
     */
    public void fetchNotificationDoc(User user) {
        CollectionReference notifcationRef = userRf.document(user.getDeviceId()).collection("notifications");
        notifcationRef
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int notifCounter = 0;
                            notifManager = NotificationManagerCompat.from(context);
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> notificationData = document.getData();
                                String notificationTitle = (String) notificationData.get("title");
                                String notificationMessage = (String) notificationData.get("message");
                                Date notificationDate = document.getDate("date");
                                String notificationEventName = (String) document.get("eventName");
                                assert notificationDate != null;
                                // Notify user if current time is after notification date
                                if(notificationDate.compareTo(Calendar.getInstance().getTime()) <= 0 && notifManager.areNotificationsEnabled()){
                                    NotificationSystem notificationSystem = new NotificationSystem(context);
                                    notificationSystem.setNotification(notifCounter,notificationEventName + ": " + notificationTitle, notificationMessage);
                                    removeNotificationDoc(document.getId(), user); // Remove notification so it is not displayed again
                                    notifCounter++;
                                }
                            }
                        } else {
                            Log.e("Firestore", "Something went wrong.");
                        }
                    }
                });
    }

    /**
     * Update User document's facility field in FireBase.
     * @Author: Kevin Li, Matthieu Larochelle
     * @Version: 1.1
     * @param user: User for which updating is required.
     */
    public void updateUserFacility(User user) {
        DocumentReference ufRf = facilityRf.document(user.getDeviceId());
        ufRf
                .get()
                .addOnSuccessListener(facilityDoc -> {
                    if (facilityDoc.exists()) {
                        Map<String, Object> fdata = new HashMap<>();
                        fdata.put("facility", ufRf);
                        userRf.document(user.getDeviceId()).update(fdata);
                        Log.d("Firestore", "Found Facility!");
                    } else {
                        Log.e("Firestore", "Didn't find Facility!");
                    }
                });
    }

    /**
     * Update User's notifications collection.
     * @Author: Matthieu Larochelle
     * @Version: 2.0
     * @param user: User for which the notification is being added.
     * @param notification: Notification which is being added.
     */
    public void updateUserNotifications(User user, Notification notification) {

        CollectionReference notifcationRef = userRf.document(user.getDeviceId()).collection("notifications");
        // Count current number of notifications
        notifcationRef
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int numNotifs = 0;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                numNotifs++;
                            }

                            Map<String, Object> data = new HashMap<>();
                            data.put("title", notification.getTitle());
                            data.put("date", notification.getTime());
                            data.put("message", notification.getMessage());
                            data.put("eventName", notification.getEvent().getEventName());
                            data.put("number", notification.getNumber());
                            notifcationRef.document(notification.getEvent().getEventName() + ", " + notification.getEvent().getOwner().getDeviceId() + ", " + numNotifs)
                                    .set(data);
                        } else {
                            Log.e("Firestore", "Something went wrong.");
                        }
                    }
                });
    }

    /**
     * Update User's notifications collection after deletion by admin.
     * @Author: Matthieu Larochelle
     * @Version: 2.0
     * @param user: User for which the notification is being added.
     * @param notification: Notification which is being added.
     */
    public void updateUserNotificationsAdmin(User user, Notification notification, String deleteType) {

        CollectionReference notifcationRef = userRf.document(user.getDeviceId()).collection("notifications");
        // Count current number of notifications
        notifcationRef
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int numNotifs = 0;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                numNotifs++;
                            }

                            Map<String, Object> data = new HashMap<>();
                            data.put("title", notification.getTitle());
                            data.put("date", notification.getTime());
                            data.put("message", notification.getMessage());
                            if (deleteType == "pictureDelete") {
                                data.put("eventName", notification.getUser().getDeviceId());
                                notifcationRef.document(notification.getUser().getDeviceId() + ", " + numNotifs)
                                        .set(data);
                            } else if (deleteType == "facilityDelete") {
                                data.put("eventName", notification.getFacility().getName());
                                notifcationRef.document(notification.getFacility().getName() + ", " + numNotifs)
                                        .set(data);
                            } else {
                                data.put("eventName", notification.getUser().getDeviceId());
                                notifcationRef.document(notification.getUser().getDeviceId() + ", " + numNotifs)
                                        .set(data);
                            }
                        } else {
                            Log.e("Firestore", "Something went wrong.");
                        }
                    }
                });
    }

    /**
     * Update User document in FireBase.
     * @Author: Kevin Li, Matthieu Larochelle
     * @Version: 1.0
     * @param user: User for which updating is required.
     */
    public void userUpdate(User user) {
        Map<String, Object> data = new HashMap<>();
        data.put("firstName", user.getFirstName());
        data.put("lastName", user.getLastName());
        data.put("email", user.getEmail());
        data.put("phoneNumber", user.getPhoneNumber());
        if(user.getLocation() != null){
            data.put("location", new GeoPoint(user.getLocation().getLatitude(), user.getLocation().getLongitude()));
        }

        userRf
                .document(user.getDeviceId())
                .update(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Firestore", "DocumentSnapshot successfully written!");
                    }
                });
    }

    /**
     * Update Facility document in FireBase.
     * @Author: Kevin Li, Matthieu Larochelle
     * @Version: 1.1
     * @param facility: Facility for which updating is required.
     */
    public void facilityUpdate(Facility facility) {
        Map<String, Object> data = new HashMap<>();
        data.put("name", facility.getName());
        data.put("details", facility.getDetails().toString());

        facilityRf
                .document(facility.getOwner().getDeviceId())
                .update(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Firestore", "DocumentSnapshot successfully written!");
                    }
                });
    }

    /**
     * Update Event document in FireBase.
     * @Author: Kevin Li, Matthieu Larochelle
     * @Version: 1.1
     * @Update: Added posterURL param
     * @param event: Event for which updating is required.
     */
    public void eventUpdate(Event event) {
        Map<String, Object> data = new HashMap<>();
        data.put("eventName", event.getEventName());
        data.put("eventDate", event.getEventDate());
        data.put("drawDate", event.getDrawDate());
        data.put("sampleSize", event.getSampleSize());
        data.put("maxEntrants", event.getMaxEntrants());
        data.put("QRHash", event.getQrCodeHash());
        data.put("posterUrl", event.getPosterUrl());

        eventRf
                .document(event.getEventName() + ", " + event.getOwner().getDeviceId())
                .update(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Firestore", "DocumentSnapshot successfully written!");
                    }
                });
    }

    /**
     * Update User document's waitlist in FireBase.
     * @Author: Kevin Li
     * @Version: 1.0
     * @param user: User for which updating is required.
     * @param event: Event for which is added.
     */
    public void updateUserWaitList(User user, Event event) {
        Log.d("FireBaseController", "Adding user: " + user.getDeviceId() + " to waitlist for event: " + event.getEventName());
        Map<String, Object> data = new HashMap<>();
        data.put("eventName", event.getEventName());
        data.put("eventDate", event.getEventDate());
        data.put("drawDate", event.getDrawDate());
        data.put("maxEntrants", event.getMaxEntrants());
        data.put("sampleSize", event.getSampleSize());
        data.put("owner", event.getOwner().getDeviceId());
        data.put("QRHash", event.getQrCodeHash());
        userRf.document(user.getDeviceId())
                .collection("waitList").document(event.getEventName()+ ", " + event.getOwner().getDeviceId()).set(data);

    }

    /**
     * Update User document's registeredevents in FireBase.
     * @Author: Kevin Li
     * @Version: 1.0
     * @param user: User for which updating is required.
     * @param event: Event for which is added.
     */
    public void updateUserRegistered(User user, Event event) {
        Map<String, Object> data = new HashMap<>();
        data.put("eventName", event.getEventName());
        data.put("eventDate", event.getEventDate());
        data.put("drawDate", event.getDrawDate());
        data.put("maxEntrants", event.getMaxEntrants());
        data.put("sampleSize", event.getSampleSize());
        data.put("owner", event.getOwner().getDeviceId());
        data.put("QRHash", event.getQrCodeHash());
        userRf.document(user.getDeviceId())
                .collection("registeredEvents").document(event.getEventName()+ ", " + event.getOwner().getDeviceId()).set(data);
    }

    /**
     * Update User document's hosted events in FireBase.
     * @Author: Matthieu Larochelle, Kevin Li
     * @Version: 1.0
     * @param user: User for which updating is required.
     * @param event: Event for which is added.
     */
    public void updateUserHosted(User user, Event event) {
        Map<String, Object> data = new HashMap<>();
        data.put("eventName", event.getEventName());
        data.put("eventDate", event.getEventDate());
        data.put("drawDate", event.getDrawDate());
        data.put("maxEntrants", event.getMaxEntrants());
        data.put("sampleSize", event.getSampleSize());
        data.put("owner", event.getOwner().getDeviceId());
        data.put("QRHash", event.getQrCodeHash());
        data.put("geoLocation", event.requiresGeolocation());
        userRf.document(user.getDeviceId())
                .collection("hostedEvents").document(event.getEventName() + ", " + event.getOwner().getDeviceId()).set(data);
    }

    /**
     * Update User document's invited events in FireBase.
     * @Author: Matthieu Larochelle, Kevin Li
     * @Version: 1.0
     * @param user: User for which updating is required.
     * @param event: Event for which is added.
     */
    public void updateUserInvited(User user, Event event) {
        Map<String, Object> data = new HashMap<>();
        data.put("eventName", event.getEventName());
        data.put("eventDate", event.getEventDate());
        data.put("drawDate", event.getDrawDate());
        data.put("maxEntrants", event.getMaxEntrants());
        data.put("sampleSize", event.getSampleSize());
        data.put("owner", event.getOwner().getDeviceId());
        data.put("QRHash", event.getQrCodeHash());
        userRf.document(user.getDeviceId())
                .collection("invitedEvents").document(event.getEventName()+ ", " + event.getOwner().getDeviceId()).set(data);
    }

    /**
     * Fetch Event by QR Code Hash
     * @Author: Edwin M
     * @Version: 1.0
     */
    public void fetchQRCodeHash(String eventName, OnSuccessListener<String> onSuccessListener) {
        eventRf.whereEqualTo("eventName", eventName)
        .get()
        .addOnSuccessListener(querySnapshot -> {
            if (!querySnapshot.isEmpty()) {
                DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                String qrCodeHash = document.getString("QRHash");
                onSuccessListener.onSuccess(qrCodeHash);
            } else {
                onSuccessListener.onSuccess(null); // Event not found
            }
        })
        .addOnFailureListener(e -> {
            Log.e("Firestore", "Error fetching QR code hash", e);
            onSuccessListener.onSuccess(null);
        });
    }

    /**
     * Delete QR Hash from Event
     * @author Kevin Li
     * @version 1.0
     */
    public void deleteQRCodeHash(Event event) {
        Map<String,Object> updates = new HashMap<>();
        updates.put("QRHash", FieldValue.delete());

        eventRf.document(event.getEventName() + ", " + event.getOwner().getDeviceId())
                .update(updates);
    }

    /**
     * Retrieves an event from Firestore using its QR code hash.
     * Resolves event details and the associated owner to construct a complete {@link Event} object.
     *
     * @author Edwin Manalastas
     * @param qrCodeHash        the QR code hash identifying the event.
     * @param onSuccessListener callback triggered with the {@link Event} if found, or {@code null} if not.
     */
    public void fetchEventByQRCodeHash(String qrCodeHash, OnSuccessListener<Event> onSuccessListener) {
        eventRf.whereEqualTo("QRHash", qrCodeHash)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        DocumentSnapshot document = querySnapshot.getDocuments().get(0);

                        // Extract fields
                        String eventName = document.getString("eventName");
                        String qrHash = document.getString("QRHash");
                        Date eventDate = document.getDate("eventDate");
                        Date drawDate = document.getDate("drawDate");
                        String eventDetails = document.getString("eventDetails");
                        Integer maxEntrants = document.getLong("maxEntrants") != null ? document.getLong("maxEntrants").intValue() : null;
                        Integer sampleSize = document.getLong("sampleSize") != null ? document.getLong("sampleSize").intValue() : null;
                        Boolean requireLocation = document.getBoolean("geoLocation") != null ? document.getBoolean("geoLocation") : false;

                        // Resolve owner reference
                        DocumentReference ownerRef = document.getDocumentReference("owner");
                        if (ownerRef != null) {
                            ownerRef.get().addOnSuccessListener(ownerDoc -> {
                                if (ownerDoc.exists()) {
                                    // Extract owner details
                                    String ownerDeviceId = ownerDoc.getId(); // deviceId as document ID
                                    User owner = new User(ownerDeviceId); // Use existing constructor

                                    owner.setDeviceId(ownerDeviceId);

                                    // Build Event object
                                    Event event = new Event();
                                    event.setEventName(eventName);
                                    event.setQrCodeHash(qrHash);
                                    event.setEventDate(eventDate);
                                    event.setDrawDate(drawDate);
                                    event.setEventDetails(eventDetails);
                                    event.setMaxEntrants(maxEntrants);
                                    event.setSampleSize(sampleSize);
                                    event.setOwner(owner);
                                    event.setRequiresGeolocation(requireLocation);

                                    // Pass the event object back
                                    onSuccessListener.onSuccess(event);
                                } else {
                                    onSuccessListener.onSuccess(null); // Owner not found
                                }
                            }).addOnFailureListener(e -> {
                                Log.e("Firestore", "Error fetching owner details", e);
                                onSuccessListener.onSuccess(null);
                            });
                        } else {
                            onSuccessListener.onSuccess(null); // No owner reference
                        }
                    } else {
                        onSuccessListener.onSuccess(null); // Event not found
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error fetching event by QR code hash", e);
                    onSuccessListener.onSuccess(null);
                });
    }


    /**
     * Fetches an Event's Wailist of users collection in Firebase.
     * @Author: Matthieu Larochelle
     * @Version: 1.0
     * @param event: Event for which fetching is required.
     */
    public void fetchEventWaitlistDoc(Event event) {
        CollectionReference colRef = eventRf.document(event.getEventName() + ", " + event.getOwner().getDeviceId()).collection("waitList");
        colRef
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> userData = document.getData();
                                User user = new User((String) userData.get("deviceId"));
                                fetchUserDoc(user);
                                event.addToWaitingEntrants(user);}
                        } else {
                            Log.e("Firestore", "Didn't find user list!");
                        }
                    }
                });
    }


    /**
     * Fetches an Event's Invited users collection in Firebase.
     * @Author: Matthieu Larochelle
     * @Version: 1.0
     * @param event: Event for which fetching is required.
     */
    public void fetchEventInvitedDoc(Event event) {
        CollectionReference colRef = eventRf.document(event.getEventName() + ", " + event.getOwner().getDeviceId()).collection("invitedEntrants");
        colRef
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> userData = document.getData();
                                User user = new User((String) userData.get("deviceId"));
                                fetchUserDoc(user);
                                event.addInvited(user);}
                        } else {
                            Log.e("Firestore", "Didn't find user list!");
                        }
                    }
                });
    }

    /**
     * Fetches an Event's Cancelled users collection in Firebase.
     * @Author: Matthieu Larochelle
     * @Version: 1.0
     * @param event: Event for which fetching is required.
     */
    public void fetchEventCancelledDoc(Event event) {
        CollectionReference colRef = eventRf.document(event.getEventName() + ", " + event.getOwner().getDeviceId()).collection("notGoing");
        colRef
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> userData = document.getData();
                                User user = new User((String) userData.get("deviceId"));
                                fetchUserDoc(user);
                                event.addToNotGoing(user);}
                        } else {
                            Log.e("Firestore", "Didn't find user list!");
                        }
                    }
                });
    }

    /**
     * Fetches an Event's Attendees collection in Firebase.
     * @Author: Matthieu Larochelle
     * @Version: 1.0
     * @param event: Event for which fetching is required.
     */
    public void fetchEventAttendeeDoc(Event event) {
        CollectionReference colRef = eventRf.document(event.getEventName() + ", " + event.getOwner().getDeviceId()).collection("attendees");
        colRef
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> userData = document.getData();
                                User user = new User((String) userData.get("deviceId"));
                                fetchUserDoc(user);
                                event.addAttendee(user);}
                        } else {
                            Log.e("Firestore", "Didn't find user list!");
                        }
                    }
                });
    }



    /**
     * Update Event document's waitlist in FireBase.
     * @Author: Kevin Li
     * @Version: 1.0
     * @param user: User for which is added.
     * @param event: Event for which updating is required.
     */
    public void updateEventWaitList(Event event, User user) {
        Log.d("FireBaseController", "Adding user: " + user.getDeviceId() + " to events waitingEntrants for event: " + event.getEventName());
        Map<String, Object> data = new HashMap<>();
        data.put("deviceId", user.getDeviceId());
        data.put("firstName", user.getFirstName());
        data.put("lastName", user.getLastName());
        data.put("email", user.getEmail());
        data.put("phoneNumber", user.getPhoneNumber());
        eventRf.document(event.getEventName() + ", " + event.getOwner().getDeviceId())
                .collection("waitList").document(user.getDeviceId()).set(data);
    }

    /**
     * Update Event document's attendees in FireBase.
     * @Author: Kevin Li
     * @Version: 1.0
     * @param user: User for which is added.
     * @param event: Event for which updating is required.
     */
    public void updateAttendees(Event event, User user) {
        Map<String, Object> data = new HashMap<>();
        data.put("deviceId", user.getDeviceId());
        data.put("firstName", user.getFirstName());
        data.put("lastName", user.getLastName());
        data.put("email", user.getEmail());
        data.put("phoneNumber", user.getPhoneNumber());
        eventRf.document(event.getEventName() + ", " + event.getOwner().getDeviceId())
                .collection("attendees").document(user.getDeviceId()).set(data);
    }

    /**
     * Update Event document's invited in FireBase.
     * @Author: Kevin Li
     * @Version: 1.0
     * @param user: User for which is added.
     * @param event: Event for which updating is required.
     */
    public void updateInvited(Event event, User user) {
        Map<String, Object> data = new HashMap<>();
        data.put("deviceId", user.getDeviceId());
        data.put("firstName", user.getFirstName());
        data.put("lastName", user.getLastName());
        data.put("email", user.getEmail());
        data.put("phoneNumber", user.getPhoneNumber());
        eventRf.document(event.getEventName() + ", " + event.getOwner().getDeviceId())
                .collection("invitedEntrants").document(user.getDeviceId())
                .set(data);
    }

    /**
     * Update Event document's not going in FireBase.
     * @Author: Kevin Li
     * @Version: 1.0
     * @param user: User for which is added.
     * @param event: Event for which updating is required.
     */
    public void updateThoseNotGoing(Event event, User user) {
        Map<String, Object> data = new HashMap<>();
        data.put("deviceId", user.getDeviceId());
        data.put("firstName", user.getFirstName());
        data.put("lastName", user.getLastName());
        data.put("email", user.getEmail());
        data.put("phoneNumber", user.getPhoneNumber());
        eventRf.document(event.getEventName() + ", " + event.getOwner().getDeviceId())
                .collection("notGoing").document(user.getDeviceId()).set(data);
    }

    /**
     * Remove user's hosted event form hostedEvents
     * @Author: Kevin Li
     * @Version: 1.0
     * @param event: Event to be removed.
     * @param user: User whose event is removed.
     */
    public void removeFromHostedEventsDoc(Event event, User user) {
        userRf.document(user.getDeviceId()).collection("hostedEvents")
                .document(event.getEventName() + ", " + user.getDeviceId())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("FireStore", "Hosted Event Successfully Deleted");
                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error deleting event, hostedEvent may not include event.", e));
    }

    /**
     * Remove Event document from waitlist, while Removing User document as well.
     * @Author: Kevin Li
     * @Version: 1.0
     * @param event: Event to be removed.
     * @param user: User to be removed.
     */
    public void removeFromWaitListDoc(Event event, User user) {
        userRf.document(user.getDeviceId()).collection("waitList").document(event.getEventName() + ", " + event.getOwner().getDeviceId())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("FireStore", "Event Successfully Deleted");
                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error deleting event, waitlist may not include event.", e));

        eventRf.document(event.getEventName() + ", " + event.getOwner().getDeviceId()).collection("waitList").document(user.getDeviceId())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("FireStore", "User Successfully Deleted");
                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error deleting user, userlist may not include user.", e));
    }

    /**
     * Remove Event document from attendee list, while Removing User document as well.
     * @Author: Kevin Li
     * @Version: 1.0
     * @param event: Event to be removed.
     * @param user: User to be removed.
     */
    public void removeAttendeeDoc(Event event, User user) {
        userRf.document(user.getDeviceId()).collection("registeredEvents").document(event.getEventName() + ", " + event.getOwner().getDeviceId())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("FireStore", "Event Successfully Deleted");
                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error deleting event, attendee list may not include event.", e));

        eventRf.document(event.getEventName() + ", " + event.getOwner().getDeviceId()).collection("attendees").document(user.getDeviceId())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("FireStore", "User Successfully Deleted");
                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error deleting user, userlist may not include user.", e));
    }

    /**
     * Remove Notifcation document from User's notifications collection.
     * @Author: Matthieu Larochelle
     * @Version: 1.0
     * @param notificationRef: Document name of the notification to be removed.
     * @param user: User who's notification is being removed.
     */
    public void removeNotificationDoc(String notificationRef, User user) {
        userRf.document(user.getDeviceId()).collection("notifications").document(notificationRef)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("FireStore", "Notification Successfully Deleted");
                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error deleting notification.", e));
    }


    /**
     * Remove User document from Invited, while Removing User document as well.
     * @Author: Kevin Li, Matthieu Larochelle
     * @Version: 1.0
     * @param event: Event to be removed.
     * @param user: User to be removed.
     */
    public void removeInvitedDoc(Event event, User user) {
        userRf.document(user.getDeviceId()).collection("invitedEvents").document(event.getEventName() + ", " + event.getOwner().getDeviceId())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("FireStore", "Event Successfully Deleted");
                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error deleting event, waitlist may not include event.", e));

        eventRf.document(event.getEventName() + ", " + event.getOwner().getDeviceId()).collection("invitedEntrants").document(user.getDeviceId())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("FireStore", "User Successfully Deleted");
                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error deleting user, userlist may not include user.", e));
    }

    /**
     * Remove User document from NotGoing.
     * @Author: Kevin Li
     * @Version: 1.0
     * @param event: Event to be removed.
     * @param user: User to be removed.
     */
    public void removeThoseNotGoingDoc(Event event, User user) {
        eventRf.document(event.getEventName() + ", " + user.getDeviceId()).collection("notGoing").document(user.getDeviceId())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("FireStore", "User Successfully Deleted");
                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error deleting user, userlist may not include user.", e));
    }

    /**
     * Remove User Document from Firebase.
     * @Author: Kevin Li
     * @Version: 1.0
     * @param user: Guy to be killed
     */
    public void removeUserDoc(User user) {
        // remove user from event's waitList and remove the user's waitlist
        userRf.document(user.getDeviceId()).collection("waitList")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.d("Firestore", "Started waitList deletion process!!!");
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> eventData = document.getData();
                                String eventName = (String) eventData.get("eventName");
                                Date eventDate = document.getDate("eventDate");
                                Date drawDate = document.getDate("drawDate");
                                Integer sampleSize = ((Long) eventData.get("sampleSize")).intValue();
                                user.createFacility();
                                Event event = new Event(user, user.getFacility(), eventName, eventDate, drawDate, sampleSize);
                                removeFromWaitListDoc(event, user);
                            }
                        } else {
                            Log.e("Firestore", "Didn't find waitlist!");
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error deleting waitlist events.", e));

        // remove user from event's Attendees and remove the user's registeredEvents list
        userRf.document(user.getDeviceId()).collection("registeredEvents")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.d("Firestore", "Started registered deletion process!!!");
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> eventData = document.getData();
                                String eventName = (String) eventData.get("eventName");
                                Date eventDate = document.getDate("eventDate");
                                Date drawDate = document.getDate("drawDate");
                                Integer sampleSize = ((Long) eventData.get("sampleSize")).intValue();
                                user.createFacility();
                                Event event = new Event(user, user.getFacility(), eventName, eventDate, drawDate, sampleSize);
                                removeAttendeeDoc(event, user);
                            }
                        } else {
                            Log.e("Firestore", "Didn't find registered!");
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error deleting registered events.", e));

        // remove user from all event's invited userlist
        eventRf.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Log.d("Firestore", "Started invited deletion process!!!");
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Map<String, Object> eventData = document.getData();
                        String eventName = (String) eventData.get("eventName");
                        Date eventDate = document.getDate("eventDate");
                        Date drawDate = document.getDate("drawDate");
                        Integer sampleSize = ((Long) eventData.get("sampleSize")).intValue();
                        user.createFacility();
                        Event event = new Event(user, user.getFacility(), eventName, eventDate, drawDate, sampleSize);
                        removeInvitedDoc(event, user);
                    }
                } else {
                    Log.e("Firestore", "Didn't find events!");
                }
            }
        });

        // remove user from all event's not going userlist
        eventRf.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Log.d("Firestore", "Started not going deletion process!!!");
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Map<String, Object> eventData = document.getData();
                        String eventName = (String) eventData.get("eventName");
                        Date eventDate = document.getDate("eventDate");
                        Date drawDate = document.getDate("drawDate");
                        Integer sampleSize = ((Long) eventData.get("sampleSize")).intValue();
                        user.createFacility();
                        Event event = new Event(user, user.getFacility(), eventName, eventDate, drawDate, sampleSize);
                        removeThoseNotGoingDoc(event, user);
                    }
                } else {
                    Log.e("Firestore", "Didn't find events!");
                }
            }
        });

        // remove user's facility and then the user
        removeFacilityDoc(user.getFacility());
        userRf.document(user.getDeviceId())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("FireStore", "The job is finished..");
                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error deleting user.", e));

    }

    /**
     * Remove Event document from Firebase.
     * @Author: Kevin Li
     * @Version: 1.0
     * @param event: Event to be eliminated.
     */
    public void removeEventDoc(Event event) {
        // remove event from user's waitlist/registered list
        eventRf.document(event.getEventName() + ", " + event.getOwner().getDeviceId()).collection("waitList")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.d("Firestore", "Started remove user from waitlist process!!!");
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> userData = document.getData();
                                User user = new User((String) userData.get("deviceId"));
                                removeFromWaitListDoc(event, user);
                            }
                        } else {
                            Log.e("Firestore", "Didn't find events!");
                        }
                    }
                });

        // remove event from user's waitlist/registered list
        eventRf.document(event.getEventName() + ", " + event.getOwner().getDeviceId()).collection("registeredEvents")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.d("Firestore", "Started remove user from waitlist process!!!");
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> userData = document.getData();
                                User user = new User((String) userData.get("deviceId"));
                                removeAttendeeDoc(event, user);
                            }
                        } else {
                            Log.e("Firestore", "Didn't find events!");
                        }
                    }
                });

        // remove event from owner's hosted list
        userRf.document(event.getOwner().getDeviceId()).collection("hostedEvents")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.d("Firestore", "Started hosted events deletion process!!!");
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                removeFromHostedEventsDoc(event, event.getOwner());
                            }
                        } else {
                            Log.e("Firestore", "Didn't find facility list!");
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error deleting facility's hosted events.", e));

        // remove event
        eventRf.document(event.getEventName() + ", " + event.getOwner().getDeviceId())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("FireStore", "EVENT Successfully Deleted!!!!!!!");
                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error deleting event.", e));
    }

    /**
     * Remove Facility document from Firebase.
     * @Author: Kevin Li
     * @Version: 1.0
     * @param facility: Facility to be finished off.
     */
    public void removeFacilityDoc(Facility facility) {
        DocumentReference docRef = userRf.document(facility.getOwner().getDeviceId());
        // remove events from user's hostedevents
        docRef.collection("hostedEvents")
                        .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.d("Firestore", "Started hosted events deletion process!!!");
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> eventData = document.getData();
                                String eventName = (String) eventData.get("eventName");
                                Date eventDate = document.getDate("eventDate");
                                Date drawDate = document.getDate("drawDate");
                                Integer sampleSize = ((Long) eventData.get("sampleSize")).intValue();
                                Event event = new Event(facility.getOwner(), facility, eventName, eventDate, drawDate,sampleSize);
                                removeFromHostedEventsDoc(event, facility.getOwner());
                                removeEventDoc(event);
                            }
                        } else {
                            Log.e("Firestore", "Didn't find facility list!");
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error deleting facility's hosted events.", e));

        // remove facility value from user
        Map<String,Object> updates = new HashMap<>();
        updates.put("facility", FieldValue.delete());
        docRef.update(updates);

        // remove facility from firebase
        facilityRf.document(facility.getOwner().getDeviceId())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("FireStore", "Facility is gone.");
                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error deleting facility.", e));
    }

    /**
     * Uploads an event poster image to Firebase Storage and retrieves its download URL.
     *
     * @author Jerry P
     * @param posterUri          the URI of the poster image to upload.
     * @param eventName          the name of the event associated with the poster.
     * @param onSuccessListener  callback triggered with the download URL upon successful upload.
     * @param onFailureListener  callback triggered in case of an upload failure.
     */
    public void uploadEventPoster(Uri posterUri, String eventName, OnSuccessListener<Uri> onSuccessListener, OnFailureListener onFailureListener) {
        // Define a storage reference for the poster
        StorageReference storageRef = FirebaseStorage.getInstance().getReference("event_posters/" + eventName);

        // Upload the file to Firebase Storage
        storageRef.putFile(posterUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Get the download URL for the uploaded image
                    storageRef.getDownloadUrl()
                            .addOnSuccessListener(onSuccessListener)
                            .addOnFailureListener(onFailureListener);
                })
                .addOnFailureListener(onFailureListener);
    }

    /**
     * Updates the poster URL of an event in Firestore.
     *
     * @author Jerry P
     * @param event the {@link Event} whose poster URL needs to be updated.
     */
    public void updateEventPoster(Event event) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String eventId = event.getEventName() + ", " + event.getOwner().getDeviceId();
        if (eventId.equals(", ")) {
            Log.e("FireBaseController", "Event ID is null or empty. Cannot update poster.");
            return;
        }

        String posterUrl = event.getPosterUrl();
        if (posterUrl == null || posterUrl.equals("no poster")) {
            posterUrl = "no poster";
            event.setPosterUrl("no poster"); // Use "no poster" as the default value
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("posterUrl", event.getPosterUrl());

        db.collection("events")
                .document(eventId)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    Log.d("FireBaseController", "Poster URL updated successfully for event: " + eventId);
                })
                .addOnFailureListener(e -> {
                    Log.e("FireBaseController", "Failed to update poster URL for event: " + eventId, e);
                });
    }

    /**
     * Gets an event's poster url (posterUrl field) from the Firebase.
     * @param event: the event who's posterUrl is being fetched.
     */
    public void fetchEventPosterUrl(Event event, OnSuccessListener<String> onSuccessListener, OnFailureListener onFailureListener) {
        eventRf.document(event.getEventName() + ", " + event.getOwner().getDeviceId())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DocumentSnapshot document = task.getResult();
                        String posterUrl = document.getString("posterUrl"); // Fetch the "posterUrl" field
                        onSuccessListener.onSuccess(posterUrl); // Pass the poster URL to the success listener
                    } else {
                        onFailureListener.onFailure(task.getException()); // Pass the exception to the failure listener
                    }
                });
    }


}
