package com.example.soroban;

import static org.junit.Assert.assertEquals;

import com.example.soroban.controller.UserController;
import com.example.soroban.model.User;

import org.junit.Test;

public class UserControllerTest {
    private User mockUser(){
        return new User("testId");
    }

    @Test
    public void testUpdateUser(){
        User newUser = mockUser();

        UserController userController = new UserController(newUser);

        userController.updateUser("Elmo", "Sesame", "elmo@gmail.com", "911");

        assertEquals("Elmo", newUser.getFirstName());
        assertEquals("Sesame", newUser.getLastName());
        assertEquals("elmo@gmail.com", newUser.getEmail());
        assertEquals(911, newUser.getPhoneNumber());
    }
}
