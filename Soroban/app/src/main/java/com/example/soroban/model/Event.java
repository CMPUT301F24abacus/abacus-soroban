package com.example.soroban.model;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import java.util.Random;
import java.util.stream.IntStream;

/**
 * Stores any relevant information that is related to an event such as
 * name, date, attendees, and facility. Manages user interaction and event participation
 * @Author: Matthieu Larochelle
 * @Version: 1.2
 * @see User
 * @see Facility
 * @see UserList
 */

public class Event implements Serializable {
    private User owner;
    private Facility facility;
    private String eventName;
    private Date eventDate;
    private Date drawDate;
    private UserList attendees;
    private UserList invitedEntrants;
    private UserList waitingEntrants;
    private UserList notGoing;
    private Integer maxEntrants;
    private Integer sampleSize;
    private String eventDetails;
    private Bitmap QRCode;
    private int numberOfNotifications = 0; // Does not need to be stored in Firebase.
    private String qrCodeHash;
    private String posterUrl;
    private boolean requiresGeolocation = false; // Set to false by default

    /**
     * No-argument constructor required by Firestore.
     */
    // No-argument constructor required by Firestore
    public Event() {
        this.attendees = new UserList();
        this.invitedEntrants = new UserList();
        this.waitingEntrants = new UserList();
        this.notGoing = new UserList();
    }

    /**
     * Constructor method for an Event.
     * @Author: Matthieu Larochelle
     * @Version: 1.0
     * @param owner : User object of the owner of the event.
     * @param facility : Facility object that the event is hosted at.
     * @param eventName : name string of the event.
     * @param eventDate : date of the event.
     * @param drawDate  : date when the participants of an event are drawn.
     * @param sampleSize: number of participants drawn.
     */
    public Event(User owner, Facility facility, String eventName, Date eventDate, Date drawDate, int sampleSize) {
        this.owner = owner;
        this.facility = facility;
        this.eventName = eventName;
        this.eventDate = eventDate;
        this.drawDate = drawDate;
        this.attendees = new UserList();
        this.invitedEntrants = new UserList();
        this.waitingEntrants = new UserList();
        this.notGoing = new UserList();
        this.sampleSize = sampleSize;
    }

    /**
     * Destructor method for Event.
     * Cleans up event resources and unregisters associated users
     * @Author: Matthieu Larochelle
     * @Version: 1.0
     */
    public void destroy(){
        for(int i = 0; i < attendees.size(); i++){
            attendees.get(i).removeRegisteredEvent(this);
        }

        for(int i = 0; i < waitingEntrants.size(); i++){
            waitingEntrants.get(i).removeFromWaitlist(this);
        }
    }

    /**
     * Getter method for an Event's owner.
     * @Author: Matthieu Larochelle
     * @Version: 1.0
     * @return: owner User object.
     */

    public User getOwner(){
        return owner;
    }

    /**
     * Setter method for an Event's owner.
     * @Author: Matthieu Larochelle
     * @Version: 1.0
     * @param owner: owner User object.
     */
    public void setOwner(User owner) {
        this.owner = owner;
    }

    /**
     * Getter method for an Event's facility.
     * @Author: Matthieu Larochelle
     * @Version: 1.0
     * @return: facility Facility object.
     */

    public Facility getFacility(){
        return facility;
    }

    /**
     * Getter method for an Event's name.
     * @Author: Matthieu Larochelle
     * @Version: 1.0
     * @return: name string.
     */
    public String getEventName() {
        return eventName;
    }

    /**
     * Setter method for an Event's name.
     * @Author: Matthieu Larochelle
     * @Version: 1.0
     * @param eventName: event's name string.
     */
    public void setEventName(String eventName) {
        this.eventName = eventName;
    }


    /**
     * Getter method for an Event's date.
     *
     * @Author: Matthieu Larochelle
     * @Version: 1.0
     * @return: event's date.
     */
    public Date getEventDate() {
        return eventDate;
    }

    /**
     * Setter method for an Event's date.
     * @Author: Matthieu Larochelle
     * @Version: 1.0
     * @param eventDate: event's date.
     */
    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    /**
     * Getter method for an Event's draw date.
     * @Author: Matthieu Larochelle
     * @Version: 1.0
     * @return: event's draw date.
     */
    public Date getDrawDate() {
        return drawDate;
    }

    /**
     * Setter method for an Event's draw date.
     * @Author: Matthieu Larochelle
     * @Version: 1.0
     * @param drawDate: event's draw date.
     */
    public void setDrawDate(Date drawDate) {
        this.drawDate = drawDate;
    }

    /**
     * Getter method for an Event's maximum number of entrants.
     * @Author: Matthieu Larochelle
     * @Version: 1.0
     * @return: maximum number of entrants integer.
     */
    public Integer getMaxEntrants() {
        return maxEntrants;
    }

    /**
     * Setter method for an Event's maximum number of entrants.
     * @Author: Matthieu Larochelle
     * @Version: 1.0
     * @param maxEntrants: maximum number of entrants integer.
     */
    public void setMaxEntrants(Integer maxEntrants) {
        this.maxEntrants = maxEntrants;
    }

    /**
     * Getter method for an Event's sample size of attendees.
     * @Author: Matthieu Larochelle
     * @Version: 1.0
     * @return: sample size of attendees integer.
     */
    public Integer getSampleSize() {
        return sampleSize;
    }

    /**
     * Setter method for an Event's sample size of attendees.
     * @Author: Matthieu Larochelle
     * @Version: 1.0
     * @param sampleSize: sample size of attendees integer.
     */
    public void setSampleSize(Integer sampleSize) {
        this.sampleSize = sampleSize;
    }

    /**
     * Getter method for an Event's requirement of geolocation.
     * @Author: Matthieu Larochelle
     * @Version: 1.0
     * @return: True if Event requires geolocation. False otherwise.
     */
    public boolean requiresGeolocation() {
        return requiresGeolocation;
    }

    /**
     * Setter method for an Event's requirement of geolocation.
     * @Author: Matthieu Larochelle
     * @Version: 1.0
     * @param requiresGeolocation True if Event requires geolocation. False otherwise.
     */
    public void setRequiresGeolocation(boolean requiresGeolocation) {
        this.requiresGeolocation = requiresGeolocation;
    }

    /**
     * Getter method for an Event's QR bitmap.
     * @Author: Kevin Li
     * @Version: 1.0
     * @return: bitmap
     */
    public Bitmap getQRCode() {
        return QRCode;
    }

    /**
     * Setter method for an Event's QR bitmap.
     * @Author: Kevin Li
     * @Version: 1.0
     * @param QRCode: qrcode of event Bitmap.
     */
    public void setQRCode(Bitmap QRCode) {
        this.QRCode = QRCode;
    }

    /**
     * Getter method for an Event's details.
     * @Author: Kevin Li
     * @Version: 1.0
     * @return: event detail String
     */
    public String getEventDetails() {
        return eventDetails;
    }

    /**
     * Setter method for an Event's details.
     * @Author: Kevin Li
     * @Version: 1.0
     * @param eventDetails: eventDetails string
     */
    public void setEventDetails(String eventDetails) {
        this.eventDetails = eventDetails;
    }

    /**
     * Getter method for an Event's Number of Notifications.
     * This is the current number of scheduled notifications an Event is outputting during this app's runtime.
     * It is not stored in Firebase and is reset when the app is reloaded.
     * @Author: Matthieu Larochelle
     * @Version: 1.0
     * @return: Number of currently scheduled notifications.
     */
    public int getNumberOfNotifications() {
        return numberOfNotifications;
    }

    /**
     * Adds one an Event's Number of Notifications.
     * This is the current number of scheduled notifications an Event is outputting during this app's runtime.
     * It is not stored in Firebase and is reset when the app is reloaded.
     * @Author: Matthieu Larochelle
     * @Version: 1.0
     * @return: Number of currently scheduled notifications.
     */
    public void addNumberOfNotifications() {
        this.numberOfNotifications  += 1;
    }

    /**
     * Add a user to an Events's list of waiting entrants.
     * @Author: Matthieu Larochelle
     * @Version: 1.2
     * @param user : User.
     * @return: Result of successful addition to Event's list of waiting entrants.
     */
    public Boolean addToWaitingEntrants(User user){
        if(this.maxEntrants == null || waitingEntrants.size() < this.maxEntrants){
            return waitingEntrants.addUser(user) && (user.getWaitList().contains(this) ? Boolean.TRUE :user.addToWaitlist(this));
        }else{
            return Boolean.FALSE;
        }
    }

    /**
     * Remove a user from an Events's list of waiting entrants.
     * @Author: Matthieu Larochelle
     * @Version: 1.2
     * @param user  User.
     * @return: Result of successful removal from Event's list of waiting entrants.
     */
    public Boolean removeFromWaitingEntrants(User user){
        return waitingEntrants.remove(user) && (user.getWaitList().contains(this) ? user.removeFromWaitlist(this)  : Boolean.TRUE);
    }


    /**
     * Getter method for Event's list of waiting entrants.
     * @Author: Matthieu Larochelle
     * @Version: 1.0
     * @Return: waiting entrant list UserList.
     */
    public UserList getWaitingEntrants() {
        return waitingEntrants;
    }


    /**
     * Add a user to an Events's list of attendees.
     * @Author: Matthieu Larochelle
     * @Version: 1.2
     * @Param: User.
     * @return: Result of successful addition to Event's list of attendees.
     */
    public Boolean addAttendee(User user){
        if(attendees.size() < this.sampleSize){
            return attendees.addUser(user) && (user.getRegisteredEvents().contains(this) ? Boolean.TRUE : user.addRegisteredEvent(this));
        }else{
            return Boolean.FALSE;
        }
    }

    /**
     * Remove a user from an Events's list of attendees.
     * @Author: Matthieu Larochelle
     * @Version: 1.2
     * @Param: User.
     * @return: Result of successful removal from Event's list of attendees.
     */
    public Boolean removeAttendee(User user){
        return attendees.remove(user) && (user.getWaitList().contains(this) ? user.removeRegisteredEvent(this) : Boolean.TRUE);
    }

    /**
     * Getter method for Event's list of attendees.
     * @Author: Matthieu Larochelle
     * @Version: 1.0
     * @Return: attendee UserList.
     */
    public UserList getAttendees() {
        return attendees;
    }

    /**
     * Add a user to an Events's list of invited.
     * @Author: Matthieu Larochelle, Kevin Li
     * @Version: 2.0
     * @param user : User.
     */
    public Boolean addInvited(User user){
        return invitedEntrants.addUser(user) && (user.getInvitedEvents().contains(this) ? Boolean.TRUE : user.addInvitedEvent(this));
    }

    /**
     * Remove a user from an Events's list of invited.
     * @Author: Kevin Li
     * @Version: 1.0
     * @param user  User.
     */
    public Boolean removeFromInvited(User user){

        return invitedEntrants.remove(user) && (user.getInvitedEvents().contains(this) ? user.removeInvitedEvent(this)  : Boolean.TRUE);
    }

    /**
     * Getter method for Event's invited.
     * @Author: Kevin Li
     * @Version: 1.0
     * @Return: invited UserList.
     */
    public UserList getInvitedEntrants() { return invitedEntrants; }

    /**
     * Add a user to an Events's list of not going entrants.
     * @Author: Kevin Li
     * @Version: 1.0
     * @param user : User.
     */
    public void addToNotGoing(User user){
        notGoing.addUser(user);
    }

    /**
     * Remove a user from an Events's list of not going.
     * @Author: Kevin Li
     * @Version: 1.0
     * @param user  User.
     */
    public void removeFromNotGoing(User user){
        notGoing.remove(user);
    }

    /**
     * Getter method for Event's entrants who are not going.
     * @Author: Kevin Li
     * @Version: 1.0
     * @Return: not going UserList.
     */
    public UserList getNotGoing() { return notGoing; }


    /**
     * Randomly samples the Event's sample size number of entrants to be invited
     * @Author: Matthieu Larochelle
     * @Version: 2.0
     * @param entrantNumber number of entrants that will be sampled (will be replaced by the size of the waitlist if said size is smaller)
     * @return: Array of indices of sampled users in the Event's InvitedEntrants list.
     */
    public ArrayList<Integer> sampleEntrants(int entrantNumber){
        Random random = new Random();
        ArrayList<Integer> sampledIndices = new ArrayList<>();
        int sampleNumber = Math.min(entrantNumber, waitingEntrants.size()); // Either sample specified number or all waitingEntrants
        if(sampleNumber == 0){
            return sampledIndices;
        }
        int newRand = random.nextInt(waitingEntrants.size());
        for(int i = 0; i < sampleNumber; i++) {
            User sampledUser = waitingEntrants.get(newRand);
            addInvited(sampledUser);
            sampledIndices.add(getInvitedEntrants().indexOf(sampledUser));
            removeFromWaitingEntrants(waitingEntrants.get(newRand));
            if(waitingEntrants.size() <= 1){
                newRand = 0;
            }else{
                newRand = random.nextInt(waitingEntrants.size());
            }
        }
        return sampledIndices;
    }


    /**
     * Checks if one Event object is equal to another..
     * @author: Matthieu Larochelle
     * @version: 1.0
     * @return
     * Return true if an Event object is equal to this Event object, or if all fields of the Event object are equal to all fields of this Event object.
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
        Event mockEvent = (Event) o;
        return Objects.equals(owner, mockEvent.getOwner()) && Objects.equals(eventName, mockEvent.getEventName()) && Objects.equals(eventDate,  mockEvent.getEventDate()) && Objects.equals(drawDate,  mockEvent.getDrawDate()) && Objects.equals(sampleSize,  mockEvent.getSampleSize()) && Objects.equals(maxEntrants,  mockEvent.getMaxEntrants());
    }

    /**
     * Getter for the QR code hash
     * @Author: Edwin M
     * @Version: 1.0
     */
    public String getQrCodeHash() {
        return qrCodeHash;
    }

    /**
     * Setter for the QR code hash
     * @Author: Edwin M
     * @Version: 1.0
     */
    public void setQrCodeHash(String qrCodeHash) {
        this.qrCodeHash = qrCodeHash;
    }

    /**
     * Getter for the poster URL
     * @Author: Jerry P
     * @Version: 1.0
     */
    public String getPosterUrl() {
        return posterUrl;
    }

    /**
     * Setter for the poster URL
     * @Author: Jerry P
     * @Version: 1.0
     */
    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }
}
