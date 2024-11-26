/**
 * Author: Ayan Chaudhry
 * References: ChatGPT, StackOverflow, GeeksForGeeks
 * Purpose: this is the notification class for the Soroban app
 */


package com.example.soroban.model;

import java.util.Date;

public class Notification {
    private String title;
    private String message;
    private Date time;
    private Event event;
    private Facility facility;
    private int number;

    public Notification(String title, String message, Date time, Event event, int number) {;
        this.title = title;
        this.message = message;
        this.time = time;
        this.event = event;
        this.number = number;
    }

    public Notification(String title, String message, Date time, Facility facility) {;
        this.title = title;
        this.message = message;
        this.time = time;
        this.facility = facility;
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

    public Facility getFacility() { return facility; }
}
