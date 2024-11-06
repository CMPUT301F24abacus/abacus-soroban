package com.example.soroban;

import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.soroban.model.Event;
import com.example.soroban.model.User;
import com.example.soroban.model.Facility;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

public class FireBaseController implements Serializable {
    FirebaseFirestore db;
    CollectionReference userRf;
    CollectionReference eventRf;
    CollectionReference facilityRf;
    CollectionReference imageRf;

    public FireBaseController(){
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
        DocumentReference facilityDoc = facilityRf.document(eventFacility.getName() + ", " + owner.getDeviceId());
        Map<String, Object> data = new HashMap<>();
        data.put("owner", userDoc);
        data.put("facility", facilityDoc);
        data.put("eventName", event.getEventName());
        data.put("eventDate", event.getEventDate());
        data.put("drawDate", event.getDrawDate());
        data.put("sampleSize", event.getSampleSize());
        data.put("maxEntrants", event.getMaxEntrants());
        data.put("QRHash", event.getQRCode());
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
        data.put("facility", facility.getName());
        data.put("details", facility.getDetails());

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
        data.put("QRHash", event.getQRCode());

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
        data.put("QRHash", event.getQRCode());
        userRf.document(user.getDeviceId())
                .collection("waitList").document(event.getEventName()).set(data);

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
        data.put("QRHash", event.getQRCode());
        userRf.document(user.getDeviceId())
                .collection("registeredEvents").document(event.getEventName()).set(data);
    }

     /**
     * Store hash data of QR code in firebase
     * @Author: Edwin M
     * @Version: 1.0
     * @param qrCodeHash: The hash generated for QRCode
     * @param event: details of event to be stored.
     */
    public void addEventWithQRCodeHash(String qrCodeHash, OrganizerEvent event) {
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("eventName", event.getName());
        eventData.put("eventDate", event.getDate());
        eventData.put("qrCodeHash", qrCodeHash);  // Store the hash in the event data

        eventRf.document(event.getName()).set(eventData)
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Event with QR code hash successfully added!"))
                .addOnFailureListener(e -> Log.e("Firestore", "Error adding event with QR code hash", e));
    }

    /**
     * Fetch Event by QR Code Hash
     * @Author: Edwin M
     * @Version: 1.0
     * @param qrCodeHash: The hash generated for QRCode
     * @param onSuccessListener: Event handling if found/not found
     */
    public void getEventByQRCodeHash(String qrCodeHash, OnSuccessListener<Event> onSuccessListener) {
        eventRf.document(qrCodeHash)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Event event = documentSnapshot.toObject(Event.class);
                        onSuccessListener.onSuccess(event);
                    } else {
                        onSuccessListener.onSuccess(null); // Event not found
                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error fetching event by QR code hash", e));
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
        eventRf.document(event.getEventName() + ", " + user.getDeviceId())
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
        eventRf.document(event.getEventName() + ", " + user.getDeviceId())
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
        eventRf.document(event.getEventName() + ", " + user.getDeviceId())
                .collection("invitedEntrants").document(user.getDeviceId()).set(data);
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
        eventRf.document(event.getEventName() + ", " + user.getDeviceId())
                .collection("notGoing").document(user.getDeviceId()).set(data);
    }
}
