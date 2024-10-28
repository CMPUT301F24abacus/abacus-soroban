package com.example.soroban;

import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;

public class FacilityController {
    private Facility facility;
    private FirebaseFirestore db;
    private CollectionReference facilityRef;

    /**
     * Constructor method for FacilityController.
     * @Author: Kevin Li
     * @Version: 1.0
     */
    public FacilityController(Facility facility) {
        db = FirebaseFirestore.getInstance();
        facilityRef = db.collection("facilities");
        this.facility = facility;
    }

    /**
     * Create a Facility.
     * @Author: Kevin Li
     * @Version: 1.0
     * @param owner: Owner of facility.
     */
    public void createFacility(User owner) {
        Facility facility = new Facility(owner);

        HashMap<String, String> data = new HashMap<>();
        data.put("Name", facility.getName());
        //data.put("Events", ...)
        facilityRef
                .document(facility.getOwner().getName())
                .set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Firestore", "DocumentSnapshot successfully written!");
                    }
                });
    }

    /**
     * Update Facility Name.
     * @Author: Kevin Li
     * @Version: 1.0
     * @param newFacilityName: New Facility name.
     */
    public void updateFacilityName(String newFacilityName) {
        facility.setName(newFacilityName);

        facilityRef
                .document(facility.getOwner().getName())
                .update("Name", newFacilityName)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Firestore", "DocumentSnapshot successfully written!");
                    }
                });
    }

}
