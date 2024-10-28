package com.example.soroban;

import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;

public class EventController {
    private Event event;
    private FirebaseFirestore db;
    private CollectionReference eventRef;

    /**
     * Constructor method for EventController.
     * @Author: Kevin Li
     * @Version: 1.0
     */
    public EventController(Event event) {
        db = FirebaseFirestore.getInstance();
        eventRef = db.collection("events");
        this.event = event;
    }

    /**
     * Create an Event.
     * @Author: Kevin Li
     * @Version: 1.0
     * @param owner: Event's owner.
     * @param eventName: Event's name.
     * @param eventDate: Event's starting date.
     * @param drawDate: Event's date of drawing participants.
     * @param sampleSize: Sample Size of Attendees
     */
    public void createEvent(User owner, String eventName, Date eventDate, Date drawDate, int sampleSize) {
        Event event = new Event(owner, owner.getFacility(), eventName, eventDate, drawDate, sampleSize);

        HashMap<String, String> data = new HashMap<>();
        data.put("Owner", event.getOwner().getDeviceId());
        data.put("Date", String.valueOf(event.getEventDate()));
        //data.put("Attendees", ...)
        eventRef
                .document(event.getEventName())
                .set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Firestore", "DocumentSnapshot successfully written!");
                    }
                });
    }


}
