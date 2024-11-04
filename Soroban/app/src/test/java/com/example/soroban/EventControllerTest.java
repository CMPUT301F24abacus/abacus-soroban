package com.example.soroban;

import org.junit.Test;

import static org.junit.Assert.*;

import com.example.soroban.controller.EventController;
import com.example.soroban.model.Event;
import com.example.soroban.model.User;

import java.util.Date;
import java.util.Optional;

public class EventControllerTest {

    private User mockUser(){
        return new User("testId");
    }

    private Event mockEvent(User owner){
        return new Event(owner, owner.createFacility(), "mockEvent", new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis()), 5);
    }

    @Test
    public void testUpdateEvent(){
        User owner = mockUser();
        Event newEvent = mockEvent(owner);

        EventController eventController = new EventController(newEvent);
        Date newDate = new Date(System.currentTimeMillis());

        eventController.updateEvent("newMockEvent",newDate, newDate, 4, 1);

        assertEquals("newMockEvent", newEvent.getEventName());
        assertEquals(newDate, newEvent.getDrawDate());
        assertEquals(newDate, newEvent.getEventDate());
        assertEquals(Integer.valueOf(4), newEvent.getSampleSize());
        assertEquals(Integer.valueOf(1), newEvent.getMaxEntrants());
    }
}
