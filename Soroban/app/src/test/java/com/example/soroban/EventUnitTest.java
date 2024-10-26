package com.example.soroban;

import org.junit.Test;

import static org.junit.Assert.*;

import java.util.Date;


public class EventUnitTest {

    private Event mockEvent(User owner){
        return new Event(owner, owner.createFacility(), "mockEvent", new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis()), 5);
    }


    @Test
    public void testDestroy(){
        User owner = new User();
        Event newEvent = mockEvent(owner);

        // Add a new entrant
        User newEntrant = new User();
        newEntrant.addToWaitlist(newEvent);
        newEvent.addToWaitingEntrants(newEntrant);

        // Add a new attendee
        User newAttendee = new User();
        newAttendee.addRegisteredEvent(newEvent);
        newEvent.addAttendee(newAttendee);

        newEvent.destroy();

        assertFalse(newEntrant.getWaitList().contains(newEvent));
        assertFalse(newAttendee.getRegisteredEvents().contains(newEvent));
    }

    @Test
    public void testAddToWaitingEntrants(){
        User owner = new User();
        Event newEvent = mockEvent(owner);

        // Add a new entrant
        User firstEntrant = new User();
        firstEntrant.setName("Elmo");
        assertTrue(newEvent.addToWaitingEntrants(firstEntrant));

        assertTrue(newEvent.getWaitingEntrants().contains(firstEntrant));

        newEvent.setMaxEntrants(1);

        // Add a new entrant
        User secondEntrant = new User();
        firstEntrant.setName("Oscar");
        assertFalse(newEvent.addToWaitingEntrants(secondEntrant));

        assertFalse(newEvent.getWaitingEntrants().contains(secondEntrant));

        newEvent.setMaxEntrants(2);

        assertTrue(newEvent.addToWaitingEntrants(secondEntrant));

        assertTrue(newEvent.getWaitingEntrants().contains(secondEntrant));
    }

    @Test
    public void testRemoveFromWaitingEntrants(){
        User owner = new User();
        Event newEvent = mockEvent(owner);

        // Add a new entrant
        User firstEntrant = new User();
        firstEntrant.setName("Elmo");
        newEvent.addToWaitingEntrants(firstEntrant);

        assertTrue(newEvent.removeFromWaitingEntrants(firstEntrant));

        assertFalse(newEvent.getWaitingEntrants().contains(firstEntrant));
    }

    @Test
    public void testAddAttendee(){
        User owner = new User();
        Event newEvent = mockEvent(owner);

        // Add a new entrant
        User firstEntrant = new User();
        firstEntrant.setName("Elmo");
        assertTrue(newEvent.addToWaitingEntrants(firstEntrant));

        assertTrue(newEvent.getWaitingEntrants().contains(firstEntrant));

        newEvent.setMaxEntrants(1);

        // Add a new entrant
        User secondEntrant = new User();
        firstEntrant.setName("Oscar");
        assertFalse(newEvent.addToWaitingEntrants(secondEntrant));

        assertFalse(newEvent.getWaitingEntrants().contains(secondEntrant));

        newEvent.setMaxEntrants(2);

        assertTrue(newEvent.addToWaitingEntrants(secondEntrant));

        assertTrue(newEvent.getWaitingEntrants().contains(secondEntrant));
    }

    @Test
    public void testRemoveAttendee(){
        User owner = new User();
        Event newEvent = mockEvent(owner);

        // Add a new entrant
        User firstEntrant = new User();
        firstEntrant.setName("Elmo");
        newEvent.addAttendee(firstEntrant);

        assertTrue(newEvent.removeAttendee(firstEntrant));

        assertFalse(newEvent.getAttendees().contains(firstEntrant));
    }

}
