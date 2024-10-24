package com.example.soroban;

import android.provider.Settings;
import android.view.View;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Stores any relevant information that is associated to a user.
 * @Author: Matthieu Larochelle
 * @Version: 1.2
 */

public class User {
    private final String deviceId;
    private String name;
    private String email;
    private int phoneNumber;
    private EventList waitList;
    private EventList registeredEvents;
    private Facility facility;

    /**
     * Constructor method for User.
     * @Author: Matthieu Larochelle
     * @Version: 1.0
     */
    public User() {
        this.deviceId = Settings.Secure.ANDROID_ID;
        this.waitList = new EventList();
        this.registeredEvents = new EventList();
    }

    /**
     * Getter method for User's device Id.
     * @Author: Matthieu Larochelle
     * @Version: 1.0
     * @Return: device Id string.
     */
    public String getDeviceId() {
        return deviceId;
    }

    /**
     * Getter method for User's name.
     * @Author: Matthieu Larochelle
     * @Version: 1.0
     * @Return: name string.
     */
    public String getName() {
        return name;
    }

    /**
     * Setter method for User's name.
     * @Author: Matthieu Larochelle
     * @Version: 1.0
     * @Param: name string.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter method for User's email address.
     * @Author: Matthieu Larochelle
     * @Version: 1.0
     * @Return: email address string.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Setter method for User's email address.
     * @Author: Matthieu Larochelle
     * @Version: 1.0
     * @Param: email address string.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Getter method for User's phone number.
     * @Author: Matthieu Larochelle
     * @Version: 1.0
     * @Return: phone number integer.
     */
    public int getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Setter method for User's phone number.
     * @Author: Matthieu Larochelle
     * @Version: 1.0
     * @Param: phone number integer.
     */
    public void setPhoneNumber(int phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Creates a User's facility. This allows the User to become an organizer and create events.
     * @Author: Matthieu Larochelle
     * @Version: 1.0
     */
    public Facility createFacility(){
        this.facility = new Facility(this);
        return this.facility;
    }

    /**
     * Returns a User's facility.
     * @Author: Matthieu Larochelle
     * @Version: 1.0
     * @Return: Facility object.
     */
    public Facility getFacility(){
        return this.facility;
    }

    /**
     * Creates a User's facility. This allows the User to become an organizer and create events.
     * @Author: Matthieu Larochelle
     * @Version: 1.0
     */
    public void removeFacility(){
        if(this.facility != null){
            this.facility.destroy();
            this.facility = null;
        }
    }

    /**
     * Getter method for User's wait list of events.
     * @Author: Matthieu Larochelle
     * @Version: 1.0
     * @Return: wait list ArrayList.
     */
    public ArrayList<Event> getWaitList() {
        return waitList.getEvents();
    }

    /**
     * Getter method for User's list of registered events.
     * @Author: Matthieu Larochelle
     * @Version: 1.0
     * @Return: registered events ArrayList.
     */
    public ArrayList<Event> getRegisteredEvents() {
        return registeredEvents.getEvents();
    }

    /**
     * Add an event to a User's waitlist.
     * @Author: Matthieu Larochelle
     * @Version: 1.1
     * @Param: Event.
     * @return : Result of successful addition to User's waitlist of events.
     */
    public Boolean addToWaitlist(Event event){
        return waitList.add(event);
    }

    /**
     * Remove an event from a User's waitlist.
     * @Author: Matthieu Larochelle
     * @Version: 1.1
     * @Param: Event.
     * @return : Result of successful removal from User's waitlist of events.
     */
    public Boolean removeFromWaitlist(Event event){
        return waitList.remove(event);
    }

    /**
     * Add an event to a User's registered events.
     * @Author: Matthieu Larochelle
     * @Version: 1.1
     * @Param: Event.
     * @return : Result of successful addition to User's list of registered events.
     */
    public Boolean addRegisteredEvent(Event event){
        return registeredEvents.add(event);
    }

    /**
     * Remove an event from a User's registered events.
     * @Author: Matthieu Larochelle
     * @Version: 1.1
     * @Param: Event.
     * @return : Result of successful removal from User's list of registered events.
     */
    public Boolean removeRegisteredEvent(Event event){
        return registeredEvents.remove(event);
    }

    /**
     * Checks if one User object is equal to another..
     * @author: Matthieu Larochelle
     * @version: 1.0
     * @return
     * Return true if a User object is equal to this User object, or if all fields of the User object are equal to all fields of this User object.
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
        User mockUser = (User) o;
        return Objects.equals(deviceId, mockUser.getDeviceId()) && Objects.equals(name, mockUser.getName()) && Objects.equals(email, mockUser.getEmail()) && Objects.equals(phoneNumber, mockUser.getPhoneNumber());
    }

}
