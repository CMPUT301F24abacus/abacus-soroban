package com.example.soroban;

import org.junit.Test;

import static org.junit.Assert.*;

import java.util.Date;


public class UserUnitTest {

    private Event mockEvent(User owner){
        return new Event(owner, "mockEvent", new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis()), 5);
    }


    @Test
    public void testCreateFacility(){
        User newUser = new User();

        Facility newFacility = newUser.createFacility();

        assertEquals(newFacility, newUser.getFacility());
    }

    @Test
    public void testRemoveFacility(){
        User newUser = new User();

        Facility newFacility = newUser.createFacility();

        newUser.removeFacility();

        assertNull(newUser.getFacility());
    }

    @Test
    public void testAddToWaitList(){
        User newUser = new User();

        Event newEvent = mockEvent(newUser);

        newUser.addToWaitlist(newEvent);

        assertEquals(newEvent, newUser.getWaitList().get(0));
    }

    @Test
    public void testRemoveFromWaitList(){
        User newUser = new User();

        Event newEvent = mockEvent(newUser);

        newUser.addToWaitlist(newEvent);

        newUser.removeFromWaitlist(newEvent);

        assertEquals(0, newUser.getWaitList().size());
        assertFalse(newUser.getWaitList().contains(newEvent));
    }

    @Test
    public void testAddRegisteredEvent(){
        User newUser = new User();

        Event newEvent = mockEvent(newUser);

        newUser.addRegisteredEvent(newEvent);

        assertEquals(newEvent, newUser.getRegisteredEvents().get(0));
    }

    @Test
    public void testRemoveRegisteredEvent(){
        User newUser = new User();

        Event newEvent = mockEvent(newUser);

        newUser.addRegisteredEvent(newEvent);

        newUser.removeRegisteredEvent(newEvent);

        assertEquals(0, newUser.getRegisteredEvents().size());
        assertFalse(newUser.getRegisteredEvents().contains(newEvent));
    }

}
