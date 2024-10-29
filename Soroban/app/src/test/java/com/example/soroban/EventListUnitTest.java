package com.example.soroban;

import org.junit.Test;

import static org.junit.Assert.*;

import java.util.Date;

public class EventListUnitTest {

    private User mockUser(){
        return new User("testId");
    }

    private Event mockEvent(User owner){
        return new Event(owner, owner.createFacility(), "mockEvent", new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis()), 5);
    }


}