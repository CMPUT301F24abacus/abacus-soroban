/**
 * Author: Ayan Chaudhry
 * References: ChatGPT, StackOverflow, GeeksForGeeks
 * Purpose: this is the notification class for the Soroban app
 */


package com.example.soroban.model;

public class Notification {
    private String title;
    private String time;
    private String event;
    private boolean isMuted;
    public enum NotificationType { BAD_NEWS, SPOT_CONFIRMED, REGISTRATION_OPEN }
    private NotificationType type;

    public Notification(String title, String time, String event, NotificationType type) {;
        this.title = title;
        this.time = time;
        this.event = event;
        this.type = type;
        this.isMuted = false; // Default to not muted
    }

    public String getTitle() {
        return title;
    }

    public String getTime() {
        return time;
    }

    public String getEvent() {
        return event;
    }

    public NotificationType getType() {
        return type;
    }

    public boolean isMuted() {
        return isMuted;
    }

    public void setMuted(boolean muted) {
        this.isMuted = muted;
    }


}