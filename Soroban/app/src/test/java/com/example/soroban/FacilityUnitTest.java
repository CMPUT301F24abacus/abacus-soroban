package com.example.soroban;

import static org.junit.Assert.*;

import org.junit.Test;

import java.util.Date;

public class FacilityUnitTest {
    private Event mockEvent(User owner){
        return new Event(owner, owner.createFacility(), "mockEvent", new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis()), 5);
    }


    @Test
    public void testDestroy(){
        User owner = new User();
        Facility newFacility = owner.createFacility();
        Event newEvent = mockEvent(owner);

        newFacility.addHostedEvent(newEvent);

        newFacility.destroy();

        // Hosted events are discontinued.
        assertThrows(NullPointerException.class, ()->{
            newFacility.getHostedEvents();
        });
    }

    @Test
    public void testAddHostedEvent(){
        User owner = new User();
        Facility newFacility = owner.createFacility();
        Event newEvent = mockEvent(owner);

        assertTrue(newFacility.addHostedEvent(newEvent));

        assertTrue(newFacility.getHostedEvents().contains(newEvent));
        assertFalse(newFacility.addHostedEvent(newEvent));
    }

    @Test
    public void testRemoveHostedEvent(){
        User owner = new User();
        Facility newFacility = owner.createFacility();
        Event newEvent = mockEvent(owner);

        assertFalse(newFacility.removeHostedEvent(newEvent));

        newFacility.addHostedEvent(newEvent);

        assertTrue(newFacility.removeHostedEvent(newEvent));
        assertFalse(newFacility.removeHostedEvent(newEvent));
    }
}
