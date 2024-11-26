package com.example.soroban.model;

import java.io.Serializable;
import java.util.ArrayList;


/**
 * Keeps track of event objects.
 * Supports adding, finding, and destroying events
 * @Author: Matthieu Larochelle
 * @Version: 2.0
 * @see Event
 */
public class EventList extends ArrayList<Event> implements Serializable {
 
  
    /**
     * Constructor method for EventList.
     * @Author: Matthieu Larochelle
     * @Version: 2.0
     */
    public EventList(){
        super();
    }


    /**
     * Destructor method for EventList.
     * @Author: Matthieu Larochelle
     * @Version: 1.0
     */
    public void destroy(){
        for(int i = 0; i < this.size(); i++){
            this.get(i).destroy();
        }
    }

    /**
     * Add event to EventList.
     * @Author: Matthieu Larochelle
     * @Version: 1.0
     * @Return: Result of addition of Event to Event List
     */
    public boolean addEvent(Event event){
        if(this.contains(event)){
            return false;
        }else{
            return this.add(event);
        }
    }


    /**
     * Find event in EventList.
     * @Author: Matthieu Larochelle
     * @Version: 1.0
     * @Return: Null if no matching event was found; the matching event otherwise
     */
    public Event find(Event event){
        for(int i = 0; i < this.size(); i++){
            if(this.get(i).equals(event)){
                return event;
            }
        }
        return null;
    }

}
