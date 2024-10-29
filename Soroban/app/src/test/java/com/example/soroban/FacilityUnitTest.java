package com.example.soroban;

import static org.junit.Assert.*;

import org.junit.Test;

import java.util.Date;

public class FacilityUnitTest {
    private User mockUser(){
        return new User("testId");
    }

    private Event mockEvent(User owner){
        return new Event(owner, owner.createFacility(), "mockEvent", new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis()), 5);
    }


    @Test
    public void testDestroy(){
        User owner = mockUser();
        owner.createFacility();
        Event newEvent = mockEvent(owner);

        owner.getFacility().addHostedEvent(newEvent);

        owner.getFacility().destroy();

        // Hosted events are discontinued.
        assertNull(owner.getFacility().getHostedEvents());
    }

    @Test
    public void testAddHostedEvent(){
        User owner = mockUser();
        Facility newFacility = owner.createFacility();
        Event newEvent = mockEvent(owner);

        assertTrue(newFacility.addHostedEvent(newEvent));

        assertTrue(newFacility.getHostedEvents().contains(newEvent));
        assertFalse(newFacility.addHostedEvent(newEvent));
    }

    @Test
    public void testRemoveHostedEvent(){
        User owner = mockUser();
        Facility newFacility = owner.createFacility();
        Event newEvent = mockEvent(owner);

        assertFalse(newFacility.removeHostedEvent(newEvent));

        newFacility.addHostedEvent(newEvent);

        assertTrue(newFacility.removeHostedEvent(newEvent));
        assertFalse(newFacility.removeHostedEvent(newEvent));
    }
}
