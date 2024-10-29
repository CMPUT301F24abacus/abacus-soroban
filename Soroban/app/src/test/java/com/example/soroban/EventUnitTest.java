package com.example.soroban;

import org.junit.Test;

import static org.junit.Assert.*;

import java.util.Date;


public class EventUnitTest {

    private User mockUser(){
        return new User("testId");
    }

    private Event mockEvent(User owner){
        return new Event(owner, owner.createFacility(), "mockEvent", new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis()), 5);
    }


    @Test
    public void testDestroy(){
        User owner = mockUser();
        Event newEvent = mockEvent(owner);

        // Add a new entrant
        User newEntrant = mockUser();
        newEntrant.addToWaitlist(newEvent);
        newEvent.addToWaitingEntrants(newEntrant);

        // Add a new attendee
        User newAttendee = mockUser();
        newAttendee.addRegisteredEvent(newEvent);
        newEvent.addAttendee(newAttendee);

        newEvent.destroy();

        assertFalse(newEntrant.getWaitList().contains(newEvent));
        assertFalse(newAttendee.getRegisteredEvents().contains(newEvent));
    }

    @Test
    public void testAddToWaitingEntrants(){
        User owner = mockUser();
        Event newEvent = mockEvent(owner);

        // Add a new entrant
        User firstEntrant = mockUser();
        firstEntrant.setName("Elmo");
        assertTrue(newEvent.addToWaitingEntrants(firstEntrant));

        assertTrue(newEvent.getWaitingEntrants().contains(firstEntrant));

        newEvent.setMaxEntrants(1);

        // Add a new entrant
        User secondEntrant = mockUser();
        firstEntrant.setName("Oscar");
        assertFalse(newEvent.addToWaitingEntrants(secondEntrant));

        assertFalse(newEvent.getWaitingEntrants().contains(secondEntrant));

        newEvent.setMaxEntrants(2);

        assertTrue(newEvent.addToWaitingEntrants(secondEntrant));
        assertTrue(newEvent.getWaitingEntrants().contains(secondEntrant));
    }

    @Test
    public void testRemoveFromWaitingEntrants(){
        User owner = mockUser();
        Event newEvent = mockEvent(owner);

        // Add a new entrant
        User firstEntrant = mockUser();
        firstEntrant.setName("Elmo");
        newEvent.addToWaitingEntrants(firstEntrant);

        assertTrue(newEvent.removeFromWaitingEntrants(firstEntrant));
        assertFalse(newEvent.getWaitingEntrants().contains(firstEntrant));
    }

    @Test
    public void testAddAttendee(){
        User owner = mockUser();
        Event newEvent = mockEvent(owner);

        // Add a new entrant
        User firstEntrant = mockUser();
        firstEntrant.setName("Elmo");
        assertTrue(newEvent.addToWaitingEntrants(firstEntrant));

        assertTrue(newEvent.getWaitingEntrants().contains(firstEntrant));

        newEvent.setMaxEntrants(1);

        // Add a new entrant
        User secondEntrant = mockUser();
        firstEntrant.setName("Oscar");
        assertFalse(newEvent.addToWaitingEntrants(secondEntrant));

        assertFalse(newEvent.getWaitingEntrants().contains(secondEntrant));

        newEvent.setMaxEntrants(2);

        assertTrue(newEvent.addToWaitingEntrants(secondEntrant));

        assertTrue(newEvent.getWaitingEntrants().contains(secondEntrant));
    }

    @Test
    public void testRemoveAttendee(){
        User owner = mockUser();
        Event newEvent = mockEvent(owner);

        // Add a new entrant
        User firstEntrant = mockUser();
        firstEntrant.setName("Elmo");
        newEvent.addAttendee(firstEntrant);

        assertTrue(newEvent.removeAttendee(firstEntrant));

        assertFalse(newEvent.getAttendees().contains(firstEntrant));
    }

}
