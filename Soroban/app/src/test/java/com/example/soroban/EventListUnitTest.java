package com.example.soroban;

import org.junit.Test;

import static org.junit.Assert.*;

import java.util.Date;

public class EventListUnitTest {

    private Event mockEvent(User owner){
        return new Event(owner, owner.createFacility(), "mockEvent", new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis()), 5);
    }

    @Test
    public void testAddEvent(){
        EventList newEventList = new EventList();
        User owner = new User();
        Event newEvent = mockEvent(owner);

        assertTrue(newEventList.add(newEvent));
        assertTrue(newEventList.getEvents().contains(newEvent));
        assertFalse(newEventList.add(newEvent));
    }

    @Test
    public void testRemoveEvent(){
        EventList newEventList = new EventList();
        User owner = new User();
        Event newEvent = mockEvent(owner);

        newEventList.add(newEvent);

        assertTrue(newEventList.remove(newEvent));
        assertFalse(newEventList.getEvents().contains(newEvent));

        assertFalse(newEventList.remove(newEvent));
    }

    @Test
    public void testGet(){
        EventList newEventList = new EventList();
        User owner = new User();
        Event newEvent = mockEvent(owner);

        newEventList.add(newEvent);

        assertEquals(newEvent, newEventList.get(0));

        newEventList.remove(newEvent);

        assertNull(newEventList.get(0));

    }

    @Test
    public void testSize(){
        EventList newEventList = new EventList();
        User owner = new User();
        Event firstEvent = mockEvent(owner);

        firstEvent.setEventName("Elmo's party");

        newEventList.add(firstEvent);

        assertEquals(1, newEventList.size());

        Event secondEvent = mockEvent(owner);

        secondEvent.setEventName("Elmo's party");

        newEventList.add(secondEvent);

        assertEquals(1, newEventList.size());

        secondEvent.setEventName("Oscar's party");

        newEventList.add(secondEvent);

        assertEquals(2, newEventList.size());

        newEventList.remove(secondEvent);

        assertEquals(1, newEventList.size());
    }

}