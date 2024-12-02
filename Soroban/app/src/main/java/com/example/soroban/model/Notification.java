package com.example.soroban.model;

import java.util.Date;

/**
 * Represents a notification associated with an event and user.
 * Stores notification title, message, and associated metadata.
 * References: ChatGPT, StackOverflow, GeeksForGeeks
 * @author Matthieu Larochelle
 * @author Ayan Chaudhry
 * @Version: 1.0
 * @see Event
 */
public class Notification {
    private String title;
    private String message;
    private Date time;
    private Event event;
    private Facility facility;
    private User user;
    private int number;

    /**
     * Constructs a new notification with specified details.
     * @author Matthieu Larochelle
     * @param title the title of the notification.
     * @param message the message content.
     * @param time the timestamp of the notification.
     * @param event the event associated with the notification.
     * @param number the notification identifier.
     */
    public Notification(String title, String message, Date time, Event event, int number) {;
        this.title = title;
        this.message = message;
        this.time = time;
        this.event = event;
        this.number = number;
    }

    /**
     * Constructs a new notification with specified details.
     * @author Kevin Li
     * @param title the title of the notification.
     * @param message the message content.
     * @param time the timestamp of the notification.
     * @param facility the facility associated with the notification.
     */
    public Notification(String title, String message, Date time, Facility facility) {;
        this.title = title;
        this.message = message;
        this.time = time;
        this.facility = facility;
    }

    /**
     * Constructs a new notification with specified details.
     * @author Kevin Li
     * @param title the title of the notification.
     * @param message the message content.
     * @param time the timestamp of the notification.
     * @param user the user associated with the notification.
     */
    public Notification(String title, String message, Date time, User user) {
        this.title = title;
        this.message = message;
        this.time = time;
        this.user = user;
    }

    /**
     * Getter for the title of the notification
     * @author Matthieu Larochelle
     */
    public String getTitle() {
        return title;
    }

    /**
     * Getter for the scheduled time of the notification
     * @author Matthieu Larochelle
     */
    public Date getTime() {
        return time;
    }

    /**
     * Getter for the associated event of the notification
     * @author Matthieu Larochelle
     */
    public Event getEvent() {
        return event;
    }

    /**
     * Getter for the message of the notification
     * @author Matthieu Larochelle
     */
    public String getMessage() {
        return message;
    }

    /**
     * Getter for the number of the notification
     * @author Matthieu Larochelle
     * @deprecated
     */
    public int getNumber() {
        return number;
    }

    /**
     * Getter for the associated facility of the notification
     * @author Kevin Li
     */
    public Facility getFacility() { return facility; }

    /**
     * Getter for the associated user of the notification
     * @author Kevin Li
     */
    public User getUser() { return user; }
}
