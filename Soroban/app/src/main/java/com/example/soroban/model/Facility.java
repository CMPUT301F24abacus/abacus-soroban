package com.example.soroban.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Stores any relevant information that is associated to a facility.
 * @Author: Matthieu Larochelle
 * @Version: 1.0
 */
public class Facility implements Serializable {
    private final User owner;
    private String name;
    private EventList hostedEvents;

    /**
     * Constructor method for Facility.
     * @Author: Matthieu Larochelle
     * @Version: 1.0
     */
    public Facility(User owner){
        this.owner = owner;
        this.hostedEvents = new EventList();
    }

    /**
     * Destructor method for Facility.
     * @Author: Matthieu Larochelle
     * @Version: 1.0
     */
    public void destroy(){
        this.hostedEvents.destroy();  // Destroy all events hosted at the facility.
        this.hostedEvents = null;
    }

    /**
     * Getter method for Facility's owner.
     * @Author: Matthieu Larochelle
     * @Version: 1.0
     * @Return: owner User object.
     */
    public User getOwner() {
        return owner;
    }

    /**
     * Getter method for Facility's name.
     * @Author: Matthieu Larochelle
     * @Version: 1.0
     * @Return: name string.
     */
    public String getName() {
        return name;
    }

    /**
     * Setter method for Facility's name.
     * @Author: Matthieu Larochelle
     * @Version: 1.0
     * @param name: name string.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Add an event to a Facility's list of events.
     * @Author: Matthieu Larochelle
     * @Version: 1.1
     * @Param: Event.
     * @return : Result of successful addition to Facility's list of events.
     */
    public Boolean addHostedEvent(Event event){
        if(this.equals(event.getFacility())){
            return hostedEvents.addEvent(event);
        }else{
            return Boolean.FALSE;
        }
    }

    /**
     * Remove an event from a Facility's list of events.
     * @Author: Matthieu Larochelle
     * @Version: 1.1
     * @Param: Event.
     * @return : Result of successful removal from Facility's list of events.
     */
    public Boolean removeHostedEvent(Event event){
        if(this.equals(event.getFacility())){
            return hostedEvents.remove(event);
        }else{
            return Boolean.FALSE;
        }
    }

    /**
     * Getter method for Facility's list of hosted events.
     * @Author: Matthieu Larochelle
     * @Version: 1.0
     * @Return: hosted events ArrayList.
     */
    public ArrayList<Event> getHostedEvents() {
        return hostedEvents;
    }

    /**
     * Checks if one Facility object is equal to another.
     * @author: Matthieu Larochelle
     * @version: 1.0
     * @return
     * Return true if a Facility object is equal to this Facility object, or if all fields of the Facility object are equal to all fields of this Facility object.
     * Returns false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Facility mockFacility = (Facility) o;
        return Objects.equals(name, mockFacility.getName()) && Objects.equals(owner, mockFacility.getOwner());
    }

}
