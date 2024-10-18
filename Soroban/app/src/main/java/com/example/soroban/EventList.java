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
     * @Return: Result of successful addition of Event to list of events.
     */
    public Boolean add(Event event){
        if(eventList.contains(event)){
            return Boolean.FALSE; // Event already present in list of events.
        }else{
            return eventList.add(event);
        }
    }

    /**
     * Remove an event from an event list.
     * @Author: Matthieu Larochelle
     * @Version: 1.0
     * @Param: Event.
     * @Return: Result of successful removal of Event from list of events.
     */
    public Boolean remove(Event event){
        if(eventList.contains(event)){
            return eventList.remove(event);
        }else{
            return Boolean.FALSE; // Event is not present in list of events.
        }
    }

    /**
     * Return the number of events in the event list.
     * @Author: Matthieu Larochelle
     * @Version: 1.0
     * @return: number of events integer.
     */
    public Integer size(){
        return eventList.size();
    }

}
