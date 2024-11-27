package com.example.soroban.controller;

import com.example.soroban.model.Event;
import com.example.soroban.model.EventList;

import java.util.Date;

/**
 * Any class that deals with the modification of an EventList object must utilize this class.
 * Provides methods for adding, removing, and updating {@link Event} objects.
 * @Author: Matthieu Larochelle
 * @Version: 1.0
 */
public class EventListController {
    private EventList eventList;

    /**
     * Constructor method for {@code EventListController}.
     * @param eventList the {@link EventList} to manage.
     * @Author: Matthieu Larochelle
     * @Version: 1.0
     */
    public EventListController(EventList eventList){
        this.eventList = eventList;
    }

    /**
     * Update User in UserList.
     * @Author: Matthieu Larochelle
     * @Version: 1.0
     *
     * @param event the target {@link Event} to update.
     * @param eventName the new name of the event.
     * @param eventDate the new event date.
     * @param drawDate the new draw date.
     * @param sampleSize the new sample size.
     * @param maxEntrants the new maximum number of entrants.
     * @return {@code true} if the update was successful, {@code false} otherwise.
     */
    public Boolean updateEvent(Event event, String eventName, Date eventDate, Date drawDate, int sampleSize, int maxEntrants){
        if(eventList.contains(event)){
            event.setEventName(eventName);
            event.setEventDate(eventDate);
            event.setDrawDate(drawDate);
            event.setSampleSize(sampleSize);
            event.setMaxEntrants(maxEntrants);
            return Boolean.TRUE;
        }else{
            return Boolean.FALSE;
        }
    }

    /**
     * Adds a new Event to EventList
     * @Author: Matthieu Larochelle
     * @version : 1.0
     * @param event the {@link Event} to add.
     * @return : Result of successful addition of Event to EventList.
     */
    public Boolean addEvent(Event event){

        return eventList.addEvent(event);
    }

    /**
     * Remove Event from EventList
     * @Author: Matthieu Larochelle
     * @version : 1.0
     * @param event
     * @return : Result of successful removal of Event from EventList.
     */
    public Boolean removeEvent(Event event){
        return eventList.remove(event);
    }
}
