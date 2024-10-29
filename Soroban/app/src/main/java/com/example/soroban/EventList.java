package com.example.soroban;

import java.io.Serializable;
import java.util.ArrayList;


/**
 * Keeps track of event objects.
 * @Author: Matthieu Larochelle
 * @Version: 1.1
 */
public class EventList implements Serializable  {
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
     * Destructor method for EventList.
     * @Author: Matthieu Larochelle
     * @Version: 1.0
     */
    public void destroy(){
        for(int i = 0; i < eventList.size(); i++){
            eventList.get(i).destroy();
        }
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
    public int size(){
        return eventList.size();
    }

    /**
     * Return the event in the event list at the specified index.
     * @Author: Matthieu Larochelle
     * @Version: 1.0
     * @param index: index in the event list.
     * @return: indexed Event object.
     */
    public Event get(int index){
        if(index >= 0 && index < eventList.size()){
            return eventList.get(index);
        }else{
            return null;
        }
    }

    /**
     * Return a basic list of events.
     * @Author: Matthieu Larochelle
     * @Version: 1.0
     * @return: ArrayList of events.
     */
    public ArrayList<Event> getEvents(){
        return this.eventList;
    }

}
