package com.example.soroban;

import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FireBaseController {
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
        data.put("name", user.getName());
        data.put("email", user.getEmail());
        data.put("phoneNumber", user.getPhoneNumber());
        data.put("facility", user.getFacility());

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
        data.put("name", user.getName());
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
        userRf.document(user.getDeviceId())
                .collection("waitList").add(data);

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
        userRf.document(user.getDeviceId())
                .collection("registeredEvents").add(data);
    }





    // Does not work for now (requires too many changes to Model Classes)
    //public void checkUserExistFb(User appUser, String deviceId) {
    //userRef.document(deviceId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
    //@Override
    //public void onComplete(@NonNull Task<DocumentSnapshot> task) {
    //if (task.isSuccessful()) {
    //DocumentSnapshot document = task.getResult();
    //if (document.exists()) {
    //appUser = document.toObject(User.class);
    //Log.d("Firestore", "User data retrieved for existing device ID.");
    //} else {
    //appUser = new User(deviceId);
    //userRef.document(deviceId).set(appUser)
    //.addOnSuccessListener(new OnSuccessListener<Void>() {
    //@Override
    //public void onSuccess(Void aVoid) {
    //Log.d("Firestore", "New user created and saved to Firestore.");
    //}
    //})
    //.addOnFailureListener(new OnFailureListener() {
    //@Override
    //public void onFailure(@NonNull Exception e) {
    //Log.e("Firestore", "Error saving new user to Firestore", e);
    //}
    //});
    //}
    //} else {
    //Log.e("Firestore", "Error checking user in Firestore", task.getException());
    //}
    //}
    //});
    //}



}
