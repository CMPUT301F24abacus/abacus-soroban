package com.example.soroban;

import java.util.ArrayList;


/**
 * Keeps track of event objects.
 * @Author: Matthieu Larochelle
 * @Version: 1.1
 */
public class EventList {
    private ArrayList<Event> eventList;

    /**
     * Constructor method for EventList.
     * @Author: Matthieu Larochelle
     * @Version: 1.0
     */
    public EventList(){
        this.eventList = new ArrayList<Event>();
    }

    /**
     * Add an event to an event list.
     * @Author: Matthieu Larochelle
     * @Version: 1.0
     * @Param: Event.
     */
    public void add(Event event){
        if(eventList.contains(event)){
            throw new IllegalArgumentException("Event already present in list of events.");
        }else{
            eventList.add(event);
        }
    }

    /**
     * Remove an event from an event list.
     * @Author: Matthieu Larochelle
     * @Version: 1.0
     * @Param: Event.
     */
    public void remove(Event event){
        if(eventList.contains(event)){
            eventList.remove(event);
        }else{
            throw new IllegalArgumentException("Event is not present in list of events.");
        }
    }
}