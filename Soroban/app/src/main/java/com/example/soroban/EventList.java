package com.example.soroban;

import java.io.Serializable;
import java.util.ArrayList;


/**
 * Keeps track of event objects.
 * @Author: Matthieu Larochelle
 * @Version: 2.0
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
     */
    public boolean addEvent(Event event){
        if(this.contains(event)){
            return false;
        }else{
            return this.add(event);
        }
    }

}
