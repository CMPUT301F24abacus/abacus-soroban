package com.example.soroban.controller;


import com.example.soroban.model.Event;

import  java.util.Date;

/**
 * Any class that deals with the modification of an Event object must utilize this class.
 * Ensures centralized control for updating event details.
 * @Author: Matthieu Larochelle
 * @Version: 1.1
 * @see Event
 */
public class EventController {
    private Event event;

    /**
     * Constructor method for EventController.
     * @Author: Kevin Li, Matthieu Larochelle
     * @param event the {@link Event} object to manage.
     * @Version: 1.1
     */
    public EventController(Event event) {
        this.event = event;
    }

    /**
     * Update the details of the associated {@link Event}
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
