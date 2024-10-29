package com.example.soroban;

import org.junit.Test;

import static org.junit.Assert.*;

import android.provider.Settings;

import java.util.Date;


public class UserUnitTest {

    private User mockUser(){
        return new User("testId");
    }

    private Event mockEvent(User owner){
        return new Event(owner, owner.createFacility(),"mockEvent", new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis()), 5);
    }


    @Test
    public void testCreateFacility(){
        User newUser = mockUser();

        Facility newFacility = newUser.createFacility();

        assertEquals(newFacility, newUser.getFacility());
    }

    @Test
    public void testRemoveFacility(){
        User newUser = mockUser();

        newUser.createFacility();

        newUser.removeFacility();

        assertNull(newUser.getFacility());
    }

    @Test
    public void testAddToWaitList(){
        User newUser = mockUser();

        Event newEvent = mockEvent(newUser);

        assertTrue(newUser.addToWaitlist(newEvent));

        assertEquals(1, newUser.getWaitList().size());
        assertTrue(newUser.getWaitList().contains(newEvent));
        assertFalse(newUser.addToWaitlist(newEvent));
    }

    @Test
    public void testRemoveFromWaitList(){
        User newUser = mockUser();

        Event newEvent = mockEvent(newUser);

        newUser.addToWaitlist(newEvent);

        newUser.removeFromWaitlist(newEvent);

        assertEquals(0, newUser.getWaitList().size());
        assertFalse(newUser.getWaitList().contains(newEvent));
    }

    @Test
    public void testAddRegisteredEvent(){
        User newUser = mockUser();

        Event newEvent = mockEvent(newUser);

        assertTrue(newUser.addRegisteredEvent(newEvent));

        assertEquals(1, newUser.getRegisteredEvents().size());
        assertTrue(newUser.getRegisteredEvents().contains(newEvent));
        assertFalse(newUser.addRegisteredEvent(newEvent));
    }

    @Test
    public void testRemoveRegisteredEvent(){
        User newUser = mockUser();

        Event newEvent = mockEvent(newUser);

        newUser.addRegisteredEvent(newEvent);

        newUser.removeRegisteredEvent(newEvent);

        assertEquals(0, newUser.getRegisteredEvents().size());
        assertFalse(newUser.getRegisteredEvents().contains(newEvent));
    }

}
