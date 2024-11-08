package com.example.soroban;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.soroban.model.Event;
import com.example.soroban.model.Notification;
import com.example.soroban.model.User;
import com.example.soroban.model.Facility;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class FireBaseController implements Serializable {
    FirebaseFirestore db;
    CollectionReference userRf;
    CollectionReference eventRf;
    CollectionReference facilityRf;
    CollectionReference imageRf;
    Context context;

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
     * @param layout: Layout which will be made visible upon data retrieval.
     * @param user: User for which creating is required.
     */
    public void initialize(ProgressBar progressBar, ConstraintLayout layout, User user){
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
                    layout.setVisibility(View.VISIBLE);
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
        data.put("facility", user.getFacility());
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
     * @Version: 1.0
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
        data.put("drawDate", event.getDrawDate());
        data.put("sampleSize", event.getSampleSize());
        data.put("maxEntrants", event.getMaxEntrants());
        data.put("QRHash", event.getQrCodeHash());
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
                        user.setPhoneNumber((long) userData.get("phoneNumber"));
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
                                User owner = new User((String) eventData.get("owner"));
                                fetchUserDoc(owner);
                                Facility facility = owner.getFacility();
                                Event event = new Event(owner, facility, eventName, eventDate, drawDate,sampleSize);
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
                                User owner = new User((String) eventData.get("owner"));
                                fetchUserDoc(owner);
                                Facility facility = owner.getFacility();
                                Event event = new Event(owner, facility, eventName, eventDate, drawDate,sampleSize);
                                if (eventData.get("maxEntrants") != null) {
                                    Integer maxEntrants = ((Long) eventData.get("maxEntrants")).intValue();
                                    event.setMaxEntrants(maxEntrants);
                                }
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
                                User owner = new User((String) eventData.get("owner"));
                                fetchUserDoc(owner);
                                Facility facility = owner.getFacility();
                                Event event = new Event(owner, facility, eventName, eventDate, drawDate,sampleSize);
                                if (eventData.get("maxEntrants") != null) {
                                    Integer maxEntrants = ((Long) eventData.get("maxEntrants")).intValue();
                                    event.setMaxEntrants(maxEntrants);
                                }
                                fetchEventCancelledDoc(event);
                                fetchEventInvitedDoc(event);
                                fetchEventAttendeeDoc(event);
                                user.addInvitedEvent(event);}
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
                                Facility facility = user.getFacility();
                                Event event = new Event(user, facility, eventName, eventDate, drawDate,sampleSize);
                                if (eventData.get("maxEntrants") != null) {
                                    Integer maxEntrants = ((Long) eventData.get("maxEntrants")).intValue();
                                    event.setMaxEntrants(maxEntrants);
                                }
                                fetchEventWaitlistDoc(event);
                                fetchEventCancelledDoc(event);
                                fetchEventInvitedDoc(event);
                                fetchEventAttendeeDoc(event);
                                user.addHostedEvent(event);}
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
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> notificationData = document.getData();
                                String notificationTitle = (String) notificationData.get("title");
                                Integer notificationNumber = ((Long) notificationData.get("number")).intValue();
                                String notificationMessage = (String) notificationData.get("message");
                                Date notificationDate = document.getDate("date");
                                String notificationEventName = (String) document.get("eventName");
                                assert notificationDate != null;
                                // Notify user if current time is after notification date
                                if(notificationDate.compareTo(Calendar.getInstance().getTime()) <= 0){
                                    Log.e("Firestore", notificationEventName + notificationTitle + notificationMessage);
                                    NotificationSystem notificationSystem = new NotificationSystem(context);
                                    notificationSystem.setNotification(notificationNumber+notificationEventName.hashCode(),notificationEventName + " : " + notificationTitle, notificationMessage);
                                    removeNotificationDoc(document.getId(), user); // Remove notification so it is not displayed again
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
     * @Version: 1.0
     * @param user: User for which the notification is being added.
     * @param notification: Notification which is being added.
     */
    public void updateUserNotifications(User user, Notification notification) {
        Map<String, Object> data = new HashMap<>();
        data.put("title", notification.getTitle());
        data.put("date", notification.getTime());
        data.put("message", notification.getMessage());
        data.put("eventName", notification.getEvent().getEventName());
        data.put("number", notification.getNumber());
        userRf.document(user.getDeviceId())
                .collection("notifications").document(notification.getEvent().getEventName() + ", " + notification.getEvent().getOwner().getDeviceId() + ", " + notification.getNumber())
                .set(data);
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
     * @Version: 1.0
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
        data.put("drawDate", event.getEventDate());
        data.put("maxEntrants", event.getMaxEntrants());
        data.put("sampleSize", event.getSampleSize());
        data.put("owner", event.getOwner().getDeviceId());
        data.put("QRHash", event.getQrCodeHash());
        userRf.document(user.getDeviceId())
                .collection("hostedEvents").document(event.getEventName()+ ", " + event.getOwner().getDeviceId()).set(data);
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
        data.put("drawDate", event.getEventDate());
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

    public void fetchEventByQRCodeHash(String qrCodeHash, OnSuccessListener<Event> onSuccessListener) {
        eventRf.whereEqualTo("QRHash", qrCodeHash)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        DocumentSnapshot document = querySnapshot.getDocuments().get(0);

                        // Only retrieve specific fields
                        String eventName = document.getString("eventName");
                        String qrHash = document.getString("QRHash");
                        Date eventDate = document.getDate("eventDate");
                        String eventDetails = document.getString("eventDetails");

                        // Create a new Event object with only the necessary data
                        Event event = new Event();
                        event.setEventName(eventName);
                        event.setQrCodeHash(qrHash);
                        event.setEventDate(eventDate);
                        event.setEventDetails(eventDetails);

                        onSuccessListener.onSuccess(event);
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
        Map<String, Object> data = new HashMap<>();
        data.put("deviceId", user.getDeviceId());
        data.put("firstName", user.getFirstName());
        data.put("lastName", user.getLastName());
        data.put("email", user.getEmail());
        data.put("phoneNumber", user.getPhoneNumber());
        eventRf.document(event.getEventName() + ", " + event.getOwner().getDeviceId())
                .collection("waitingEntrants").document(user.getDeviceId()).set(data);
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



}
