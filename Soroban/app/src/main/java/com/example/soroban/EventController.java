package com.example.soroban;


import  java.util.Date;

/**
 * Any class that deals with the modification of an Event object must utilize this class.
 * @Author: Matthieu Larochelle
 * @Version: 1.1
 */
public class EventController {
    private Event event;

    /**
     * Constructor method for EventController.
     * @Author: Kevin Li, Matthieu Larochelle
     * @Version: 1.1
     */
    public EventController(Event event) {
        this.event = event;
    }

    /**
     * Update Event.
     * @Author: Matthieu Larochelle
     * @Version: 1.0
     * @param eventName: Event's new name.
     * @param eventDate: Event's new date.
     * @param drawDate: Event's new  draw date.
     * @param sampleSize: Event's new sample size.
     * @param maxEntrants : Event's new max number of entrants.
     */
    public void updateEvent(String eventName, Date eventDate, Date drawDate, int sampleSize, int maxEntrants){
        event.setEventName(eventName);
        event.setEventDate(eventDate);
        event.setDrawDate(drawDate);
        event.setSampleSize(sampleSize);
        event.setMaxEntrants(maxEntrants);
    }

}
