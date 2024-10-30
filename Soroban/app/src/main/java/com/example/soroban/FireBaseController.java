package com.example.soroban;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

public class FireBaseController {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference userRf;
    CollectionReference eventRf;
    CollectionReference facilityRf;
    CollectionReference imageRf;

    public FireBaseController(){
        userRf = db.collection("users");
        eventRf = db.collection("events");
        facilityRf = db.collection("facilities");
        imageRf = db.collection("images");
    }

    /**
     * Update User document in FireBase.
     * @Author: Kevin Li, Matthieu Larochelle
     * @Version: 1.0
     * @param firstName: User's new first name.
     * @param lastName : User's new last name.
     * @param email: User's new email address.
     * @param number: User's new phone number.
     */
    public void updateUser(String deviceID, String email, Facility facility, String name, int phoneNumber, EventList registeredEvents, EventList waitList){
        HashMap<String, Object> data = new HashMap<>();
        data.put("deviceID", deviceID);
        data.put("email", email);
        data.put("facility", facility);
        data.put("name", name);
        data.put("phoneNumber", phoneNumber);
        data.put("registeredEvents", registeredEvents);
        data.put("waitList", waitList);
        userRf.document(deviceID).set(data);
    }



}
