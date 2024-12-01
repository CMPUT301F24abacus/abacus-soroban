package com.example.soroban.model;

import org.osmdroid.util.GeoPoint;

import java.io.Serializable;
import java.util.Objects;

/**
 * Stores any relevant information that is associated to a user.
 * Manages user's roles, events, and facilities.
 *
 * @Author: Matthieu Larochelle
 * @Version: 1.4
 * @see Event
 * @see Facility
 * @see EventList
 */

public class User implements Serializable {
    private String deviceId;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private long phoneNumber;
    private EventList waitList;
    private EventList registeredEvents;
    private EventList hostedEvents;
    private EventList invitedEvents;
    private Facility facility;
    private GeoPoint location;
    private Boolean adminCheck = false;

    // Required no-argument constructor for Firestore
    public User() {
    }

    /**
     * Constructor method for User with all fields to create account.
     *
     * @param deviceId the unique identifier for the user.
     * @param firstName the user's first name.
     * @param lastName the user's last name.
     * @param email the user's email address.
     * @param phoneNumber the user's phone number.
     */
    public User(String deviceId, String firstName, String lastName, String email, long phoneNumber) {
        this.deviceId = deviceId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = 0; // optional phone number
        this.waitList = new EventList();
        this.registeredEvents = new EventList();
        this.hostedEvents = new EventList();
        this.invitedEvents = new EventList();
    }

    /**
     * Constructor method for User.
     * @Author: Matthieu Larochelle
     * @Version: 1.2
     * @param deviceId the unique identifier for the user.
     */
    public User(String deviceId) {
        this.deviceId = deviceId;
        this.waitList = new EventList();
        this.registeredEvents = new EventList();
        this.hostedEvents = new EventList();
        this.invitedEvents = new EventList();
    }

    /**
     * Destructor method for User.
     * @Author: Matthieu Larochelle
     * @Version: 1.0
     */
    public void destroy(){
        for(int i = 0; i < waitList.size(); i++){
            waitList.get(i).removeFromWaitingEntrants(this);
        }

        for(int i = 0; i < registeredEvents.size(); i++){
            registeredEvents.get(i).removeAttendee(this);
        }
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

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    /**
     * Getter method for User's username.
     * @Author: Kevin Li
     * @Version: 1.0
     * @Return: username string.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Setter method for User's username.
     * @Author: Kevin Li
     * @Version: 1.0
     * @Param: username string.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Getter method for User's first name.
     * @Author: Matthieu Larochelle
     * @Version: 1.0
     * @Return: name string.
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Setter method for User's last name.
     * @Author: Matthieu Larochelle
     * @Version: 1.0
     * @Param: name string.
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Getter method for User's last name.
     * @Author: Matthieu Larochelle
     * @Version: 1.0
     * @Return: name string.
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Setter method for User's first name.
     * @Author: Matthieu Larochelle
     * @Version: 1.0
     * @Param: name string.
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
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
     * @Return: phone number long.
     */
    public long getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Setter method for User's phone number.
     * @Author: Matthieu Larochelle
     * @Version: 1.0
     * @Param: phone number long.
     */
    public void setPhoneNumber(long phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Getter method for User's location.
     * @Author: Matthieu Larochelle
     * @Version: 1.0
     * @Return: OMS GeoPoint.
     */
    public GeoPoint getLocation() {
        return location;
    }

    /**
     * Setter method for User's location.
     * @Author: Matthieu Larochelle
     * @Version: 1.0
     * @Param: OMS GeoPoint.
     */
    public void setLocation(double lattitude, double longitude) {
        this.location = new GeoPoint(lattitude,longitude);
    }

    /**
     * Setter method for User's admin boolean check.
     * @Author: Kevin Li
     * @Version: 1.0
     * @Param: admin Boolean
     */
    public void setAdminCheck(Boolean adminCheck) {
        this.adminCheck = adminCheck;
    }

    /**
     * Getter method for User's admin boolean.
     * @Author: Kevin Li
     * @Version: 1.0
     */
    public Boolean getAdminCheck() {
        return adminCheck != null ? adminCheck : false;
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
     * @Return: wait list EventList.
     */
    public EventList getWaitList() {
        return waitList;
    }

    /**
     * Getter method for User's list of registered events.
     * @Author: Matthieu Larochelle
     * @Version: 1.0
     * @Return: registered events EventList.
     */
    public EventList getRegisteredEvents() {
        return registeredEvents;
    }

    /**
     * Getter method for User's list of invited events.
     * @Author: Matthieu Larochelle
     * @Version: 1.0
     * @Return: invited events EventList.
     */
    public EventList getInvitedEvents() {
        return invitedEvents;
    }


    /**
     * Getter method for User's list of hosted events.
     * @Author: Matthieu Larochelle
     * @Version: 1.0
     * @Return: host events EventList.
     */
    public EventList getHostedEvents() {
        return hostedEvents;
    }


    /**
     * Add an event to a User's waitlist.
     * @Author: Matthieu Larochelle
     * @Version: 1.2
     * @Param: Event.
     * @return : Result of successful addition to User's waitlist of events.
     */
    public Boolean addToWaitlist(Event event){
        return waitList.addEvent(event) && (event.getWaitingEntrants().contains(this) ? Boolean.TRUE : event.addToWaitingEntrants(this));
    }

    /**
     * Remove an event from a User's waitlist.
     * @Author: Matthieu Larochelle
     * @Version: 1.2
     * @Param: Event.
     * @return : Result of successful removal from User's waitlist of events.
     */
    public Boolean removeFromWaitlist(Event event){
        return waitList.remove(event) && (event.getWaitingEntrants().contains(this) ? event.removeFromWaitingEntrants(this) : Boolean.TRUE);
    }


    /**
     * Add an event to a User's invited events.
     * @Author: Matthieu Larochelle
     * @Version: 1.0
     * @Param: Event.
     * @return : Result of successful addition to User's list of invited events.
     */
    public Boolean addInvitedEvent(Event event){
        return invitedEvents.addEvent(event) && (event.getInvitedEntrants().contains(this) ? Boolean.TRUE : event.addInvited(this));
    }

    /**
     * Remove an event from a User's invited events.
     * @Author: Matthieu Larochelle
     * @Version: 1.0
     * @Param: Event.
     * @return : Result of successful removal from User's list of invited events.
     */
    public Boolean removeInvitedEvent(Event event){
        return invitedEvents.remove(event) && (event.getInvitedEntrants().contains(this) ? event.removeFromInvited(this) : Boolean.TRUE);
    }


    /**
     * Add an event to a User's registered events.
     * @Author: Matthieu Larochelle
     * @Version: 1.2
     * @Param: Event.
     * @return : Result of successful addition to User's list of registered events.
     */
    public Boolean addRegisteredEvent(Event event){
        return registeredEvents.addEvent(event) && (event.getAttendees().contains(this) ? Boolean.TRUE : event.addAttendee(this));
    }

    /**
     * Remove an event from a User's registered events.
     * @Author: Matthieu Larochelle
     * @Version: 1.2
     * @Param: Event.
     * @return : Result of successful removal from User's list of registered events.
     */
    public Boolean removeRegisteredEvent(Event event){
        return registeredEvents.remove(event) && (event.getAttendees().contains(this) ? event.removeAttendee(this) : Boolean.TRUE);
    }


    /**
     * Add an event to a User's hosted events.
     * @Author: Matthieu Larochelle
     * @Version: 1.0
     * @Param: Event.
     * @return : Result of successful addition to User's list of hosted events.
     */
    public Boolean addHostedEvent(Event event){
        return hostedEvents.addEvent(event);
    }

    /**
     * Remove an event from a User's hosted events.
     * @Author: Matthieu Larochelle
     * @Version: 1.0
     * @Param: Event.
     * @return : Result of successful removal from User's list of hosted events.
     */
    public Boolean removeHostedEvent(Event event){
        return hostedEvents.remove(event);
    }

    /**
     * Checks if one User object is equal to another.
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
        return Objects.equals(deviceId, mockUser.getDeviceId()) && Objects.equals(firstName, mockUser.getFirstName()) && Objects.equals(lastName, mockUser.getLastName()) && Objects.equals(email, mockUser.getEmail()) && Objects.equals(phoneNumber, mockUser.getPhoneNumber());
    }

}
