package com.example.soroban;

import android.provider.Settings;
import android.view.View;

import java.security.InvalidParameterException;
import java.util.ArrayList;

/**
 * Stores any relevant information that is associated to a user.
 * @Author: Matthieu Larochelle
 * @Version: 1.1
 */

public class User {
    private final String deviceId;
    private String name;
    private String email;
    private int phoneNumber;
    private ArrayList<View> views;
    private EventList waitList;
    private EventList registeredEvents;

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
     * Add an event to a User's waitlist.
     * @Author: Matthieu Larochelle
     * @Version: 1.0
     * @Param: Event.
     */
    public void addToWaitlist(Event event){
        waitList.add(event);
    }

    /**
     * Remove an event from a User's waitlist.
     * @Author: Matthieu Larochelle
     * @Version: 1.0
     * @Param: Event.
     */
    public void removeFromWaitlist(Event event){
        waitList.remove(event);
    }

    /**
     * Add an event to a User's registered events.
     * @Author: Matthieu Larochelle
     * @Version: 1.0
     * @Param: Event.
     */
    public void addRegisteredEvent(Event event){
        registeredEvents.add(event);
    }

    /**
     * Remove an event from a User's registered events.
     * @Author: Matthieu Larochelle
     * @Version: 1.0
     * @Param: Event.
     */
    public void removeRegisteredEvent(Event event){
        registeredEvents.remove(event);
    }


}
