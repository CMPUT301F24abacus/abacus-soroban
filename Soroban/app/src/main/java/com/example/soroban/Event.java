package com.example.soroban;

import java.util.Date;

/**
 * @Author: Matthieu Larochelle
 * @Version: 1.0
 */

public class Event {
    private String eventName;
    private Date eventDate;
    private Date drawDate;

    /**
     * Constructor method for an Event.
     * @Author: Matthieu Larochelle
     * @Version: 1.0
     * @param eventName : name string of the event.
     * @param eventDate : date of the event.
     * @param drawDate  : date when the participants of an event are drawn.
     */
    public Event(String eventName, Date eventDate, Date drawDate) {
        this.eventName = eventName;
        this.eventDate = eventDate;
        this.drawDate = drawDate;
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
}
