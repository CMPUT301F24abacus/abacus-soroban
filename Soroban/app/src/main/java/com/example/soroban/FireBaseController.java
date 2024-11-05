package com.example.soroban;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.soroban.model.Event;
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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
     * Create a new User document in Firebase.
     * @Author: Kevin Li
     * @Version: 1.0
     * @param user: User for which updating is required.
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
                        if (facilityDocRef != null) { fetchFacilityDoc(user, facilityDocRef); }
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
                        Map<String, Object> facilityData = documentFacility.getData();
                        assert facilityData != null;
                        user.createFacility();
                        String name = (String) facilityData.get("name");
                        user.getFacility().setName(name);
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
                                user.addRegisteredEvent(event);
                            }
                        } else {
                            Log.e("Firestore", "Didn't find eventList!");
                        }
                    }
                });
    }

    /**
     * Update User document's facility field in FireBase.
     * @Author: Kevin Li
     * @Version: 1.0
     * @param user: User for which updating is required.
     */
    public void updateUserFacility(User user) {
        DocumentReference ufRf = facilityRf.document(user.getFacility().getName() + ", " + user.getDeviceId());
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
     * Update User document's waitlist in FireBase.
     * @Author: Kevin Li
     * @Version: 1.0
     * @param user: User for which updating is required.
     */
    public void updateUserWaitList(User user, Event event) {
        Map<String, Object> data = new HashMap<>();
        data.put("eventName", event.getEventName());
        data.put("eventDate", event.getEventDate());
        data.put("drawDate", event.getDrawDate());
        data.put("maxEntrants", event.getMaxEntrants());
        data.put("sampleSize", event.getSampleSize());
        data.put("owner", event.getOwner().getDeviceId());
        userRf.document(user.getDeviceId())
                .collection("waitList").document(event.getEventName()).set(data);

    }

    /**
     * Update User document's registeredevents in FireBase.
     * @Author: Kevin Li
     * @Version: 1.0
     * @param user: User for which updating is required.
     */
    public void updateUserRegistered(User user, Event event) {
        Map<String, Object> data = new HashMap<>();
        data.put("eventName", event.getEventName());
        data.put("eventDate", event.getEventDate());
        data.put("drawDate", event.getDrawDate());
        data.put("maxEntrants", event.getMaxEntrants());
        data.put("sampleSize", event.getSampleSize());
        data.put("owner", event.getOwner().getDeviceId());
        userRf.document(user.getDeviceId())
                .collection("registeredEvents").document(event.getEventName()).set(data);
    }


}
