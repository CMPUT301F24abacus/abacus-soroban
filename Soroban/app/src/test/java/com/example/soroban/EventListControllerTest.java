package com.example.soroban;

import org.junit.Test;

import static org.junit.Assert.*;

import android.util.Log;

import com.example.soroban.controller.EventController;
import com.example.soroban.controller.EventListController;
import com.example.soroban.model.Event;
import com.example.soroban.model.EventList;
import com.example.soroban.model.User;

import java.util.Date;

public class EventListControllerTest {

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
        EventList eventList = new EventList();

        eventList.add(newEvent);

        EventListController eventListController = new EventListController(eventList);
        Date newDate = new Date(System.currentTimeMillis());

        eventListController.updateEvent(newEvent,"newMockEvent", newDate, newDate, 4, 1);

        assertEquals("newMockEvent", newEvent.getEventName());
        assertEquals(newDate, newEvent.getDrawDate());
        assertEquals(newDate, newEvent.getEventDate());
        assertEquals(Integer.valueOf(4), newEvent.getSampleSize());
        assertEquals(Integer.valueOf(1), newEvent.getMaxEntrants());
    }

    @Test
    public void testAddEvent(){
        User owner = mockUser();
        Event newEvent = mockEvent(owner);
        EventList eventList = new EventList();

        EventListController eventListController = new EventListController(eventList);

        assertTrue(eventListController.addEvent(newEvent));
        assertTrue(eventList.contains(newEvent));
        assertFalse(eventListController.addEvent(newEvent));
    }

    @Test
    public void testRemoveEvent(){
        User owner = mockUser();
        Event newEvent = mockEvent(owner);
        EventList eventList = new EventList();

        EventListController eventListController = new EventListController(eventList);

        assertFalse(eventListController.removeEvent(newEvent));
        eventListController.addEvent(newEvent);

        assertTrue(eventListController.removeEvent(newEvent));
        assertFalse(eventList.contains(newEvent));
    }

}
