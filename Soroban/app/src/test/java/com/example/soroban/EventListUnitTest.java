package com.example.soroban;

import com.example.soroban.model.Event;
import com.example.soroban.model.User;

import java.util.Date;

public class EventListUnitTest {

    private User mockUser(){
        return new User("testId");
    }

    private Event mockEvent(User owner){
        return new Event(owner, owner.createFacility(), "mockEvent", new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis()), 5);
    }


}