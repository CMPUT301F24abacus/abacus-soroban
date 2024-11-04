package com.example.soroban;

import org.junit.Test;

import static org.junit.Assert.*;

import com.example.soroban.controller.FacilityController;
import com.example.soroban.model.Facility;
import com.example.soroban.model.User;

import java.util.Date;

public class FacilityControllerTest {
    private User mockUser(){
        return new User("testId");
    }


    @Test
    public void testUpdateFacility(){
        User owner = mockUser();
        Facility newFacility = new Facility(owner);

        FacilityController FacilityController = new FacilityController(newFacility);
        Date newDate = new Date(System.currentTimeMillis());

        FacilityController.updateFacility("Sesame street library");

        assertEquals("Sesame street library", newFacility.getName());
    }
}
