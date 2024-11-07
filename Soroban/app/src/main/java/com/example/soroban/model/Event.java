package com.example.soroban.model;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * Stores any relevant information that is related to an event.
 * @Author: Matthieu Larochelle
 * @Version: 1.2
 */

public class Event implements Serializable {
    private final User owner;
    private final Facility facility;
    private String eventName;
    private Date eventDate;
    private Date drawDate;
    private final UserList attendees;
    private final UserList invitedEntrants;
    private final UserList waitingEntrants;
    private final UserList notGoing;
    private Integer maxEntrants;
    private Integer sampleSize;
    private String eventDetails;
    private Bitmap QRCode;


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

    public String getEventDetails() {
        return eventDetails;
    }

    public void setEventDetails(String eventDetails) {
        this.eventDetails = eventDetails;
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
     * @Author: Kevin Li
     * @Version: 1.0
     * @param user : User.
     */
    public void addInvited(User user){
        invitedEntrants.addUser(user);
    }

    /**
     * Remove a user from an Events's list of invited.
     * @Author: Kevin Li
     * @Version: 1.0
     * @param user  User.
     */
    public void removeFromInvited(User user){
        invitedEntrants.remove(user);
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


}
