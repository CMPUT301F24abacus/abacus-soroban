package com.example.soroban;

// Event.java
public class OrganizerEvent {
    private String name;
    private String date;

    public OrganizerEvent(String name, String date) {
        this.name = name;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }
}
