package com.example.soroban.model;

import java.util.Date;

/**
 * Represents a notification associated with an event and user.
 * Stores notification title, message, and associated metadata.
 * References: ChatGPT, StackOverflow, GeeksForGeeks
 *
 * @author Ayan Chaudhry
 * @Version: 1.0
 * @see Event
 */
public class Notification {
    private String title;
    private String message;
    private Date time;
    private Event event;
    private int number;

    /**
     * Constructs a new notification with specified details.
     *
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

    public String getTitle() {
        return title;
    }

    public Date getTime() {
        return time;
    }

    public Event getEvent() {
        return event;
    }

    public String getMessage() {
        return message;
    }

    public int getNumber() {
        return number;
    }
}
