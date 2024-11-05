package com.example.soroban;

import com.example.soroban.controller.UserListController;
import com.example.soroban.model.User;
import com.example.soroban.model.UserList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import org.junit.Test;


public class UserListControllerTest {
    private User mockUser(){
        return new User("testId");
    }

    @Test
    public void testUpdateUser(){
        User newUser = mockUser();
        UserList newUserList = new UserList();
        newUserList.addUser(newUser);

        UserListController userListController = new UserListController(newUserList);
        userListController.updateUser(newUser,"Elmo", "Sesame", "elmo@gmail.com", "911" );

        assertEquals("Elmo", newUser.getFirstName());
        assertEquals("Sesame", newUser.getLastName());
        assertEquals("elmo@gmail.com", newUser.getEmail());
        assertEquals(911, newUser.getPhoneNumber());
    }

    @Test
    public void testAddUser(){
        User newUser = mockUser();
        UserList newUserList = new UserList();

        UserListController userListController = new UserListController(newUserList);

        assertTrue(userListController.addUser(newUser));
        assertTrue(newUserList.contains(newUser));
        assertFalse(userListController.addUser(newUser));
    }


    @Test
    public void testRemoveUser(){
        User newUser = mockUser();
        UserList newUserList = new UserList();

        UserListController userListController = new UserListController(newUserList);

        assertFalse(userListController.removeUser(newUser));
        userListController.addUser(newUser);

        assertTrue(userListController.removeUser(newUser));
        assertFalse(newUserList.contains(newUser));
    }

}
