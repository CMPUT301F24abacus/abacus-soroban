package com.example.soroban.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Stores any relevant information that is associated to a facility.
 * Manages facility-specific details and hosted events.
 * @Author: Matthieu Larochelle
 * @Version: 2.0
 * @see Event
 * @see EventList
 */
public class Facility {
    private final User owner;
    private String name;
    private EventList hostedEvents;
    private String details;

    /**
     * Constructor method for Facility.
     *
     * @Author: Matthieu Larochelle
     * @Version: 2.0
     * @param owner the owner of the facility.
     */
    public Facility(User owner) {
        User mockUser = new User(owner.getDeviceId());

        // Create mock version of Owner to avoid Serialization cycles

        if(!(mockUser.getUsername() == null)){
            mockUser.setUsername(mockUser.getUsername());
        }
        if(!(mockUser.getFirstName() == null)){
            mockUser.setFirstName(mockUser.getFirstName());
        }
        if(!(mockUser.getLastName() == null)){
            mockUser.setFirstName(mockUser.getLastName());
        }
        if(!(mockUser.getEmail() == null)){
            mockUser.setFirstName(mockUser.getEmail());
        }
        if(!(Long.valueOf(mockUser.getPhoneNumber()) == null)){
            mockUser.setPhoneNumber(mockUser.getPhoneNumber());
        }
        if(!(mockUser.getLocation() == null)){
            mockUser.setLocation(mockUser.getLocation().getLatitude(),mockUser.getLocation().getLongitude());
        }

        mockUser.setAdminCheck(owner.getAdminCheck());

        for(int i = 0; i < owner.getWaitList().size(); i++ ){
            mockUser.addToWaitlist(owner.getWaitList().get(i));
        }

        for(int i = 0; i < owner.getRegisteredEvents().size(); i++ ){
            mockUser.addRegisteredEvent(owner.getRegisteredEvents().get(i));
        }

        for(int i = 0; i < owner.getInvitedEvents().size(); i++ ){
            mockUser.addInvitedEvent(owner.getInvitedEvents().get(i));
        }

        for(int i = 0; i < owner.getHostedEvents().size(); i++ ){
            mockUser.addHostedEvent(owner.getHostedEvents().get(i));
        }

        this.owner = mockUser;
        this.hostedEvents = new EventList();
    }

    public Facility(String updatedFacilityName, String updatedFacilityDetails, User owner) {
        this.name = updatedFacilityName;
        this.owner = owner;
        this.hostedEvents = new EventList();
    }


    /**
     * Destructor method for Facility.
     *
     * @Author: Matthieu Larochelle
     * @Version: 1.0
     */
    public void destroy() {
        this.hostedEvents.destroy();  // Destroy all events hosted at the facility.
        this.hostedEvents = null;
    }

    /**
     * Getter method for Facility's owner.
     *
     * @Author: Matthieu Larochelle
     * @Version: 1.0
     * @Return: owner User object.
     */
    public User getOwner() {
        return owner;
    }

    /**
     * Getter method for Facility's name.
     *
     * @Author: Matthieu Larochelle
     * @Version: 1.0
     * @Return: name string.
     */
    public String getName() {
        return name;
    }

    /**
     * Setter method for Facility's name.
     *
     * @param name: name string.
     * @Author: Matthieu Larochelle
     * @Version: 1.0
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Add an event to a Facility's list of events.
     *
     * @return : Result of successful addition to Facility's list of events.
     * @Author: Matthieu Larochelle
     * @Version: 1.1
     * @Param: Event.
     */
    public Boolean addHostedEvent(Event event) {
        if (this.equals(event.getFacility())) {
            return hostedEvents.addEvent(event);
        } else {
            return Boolean.FALSE;
        }
    }

    /**
     * Remove an event from a Facility's list of events.
     *
     * @return : Result of successful removal from Facility's list of events.
     * @Author: Matthieu Larochelle
     * @Version: 1.1
     * @Param: Event.
     */
    public Boolean removeHostedEvent(Event event) {
        if (this.equals(event.getFacility())) {
            return hostedEvents.remove(event);
        } else {
            return Boolean.FALSE;
        }
    }

    /**
     * Getter method for Facility's list of hosted events.
     *
     * @Author: Matthieu Larochelle
     * @Version: 1.0
     * @Return: hosted events ArrayList.
     */
    public ArrayList<Event> getHostedEvents() {
        return hostedEvents;
    }

    /**
     * Checks if one Facility object is equal to another.
     *
     * @return Return true if a Facility object is equal to this Facility object, or if all fields of the Facility object are equal to all fields of this Facility object.
     * Returns false otherwise.
     * @author: Matthieu Larochelle
     * @version: 1.0
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


    public CharSequence getDetails() {
        return this.details;
    }

    public void setDetails(String updatedFacilityDetails) {
        if (updatedFacilityDetails != null) {
            this.details = updatedFacilityDetails;
        }
    }
}