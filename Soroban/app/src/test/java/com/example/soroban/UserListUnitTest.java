package com.example.soroban;

import org.junit.Test;

import static org.junit.Assert.*;

public class UserListUnitTest {

    @Test
    public void testAddUser(){
        UserList newUserList = new UserList();
        User newUser = new User();

        assertTrue(newUserList.add(newUser));
        assertTrue(newUserList.getUsers().contains(newUser));
        assertFalse(newUserList.add(newUser));
    }

    @Test
    public void testRemoveUser(){
        UserList newUserList = new UserList();
        User newUser = new User();

        newUserList.add(newUser);

        assertTrue(newUserList.remove(newUser));
        assertFalse(newUserList.getUsers().contains(newUser));

        assertFalse(newUserList.remove(newUser));
    }

    @Test
    public void testGet(){
        UserList newUserList = new UserList();
        User newUser = new User();

        newUserList.add(newUser);

        assertEquals(newUser, newUserList.get(0));

        newUserList.remove(newUser);

        assertNull(newUserList.get(0));

    }

    @Test
    public void testSize(){
        UserList newUserList = new UserList();
        User firstUser = new User();

        firstUser.setName("Elmo");

        newUserList.add(firstUser);

        assertEquals(1, newUserList.size());

        User secondUser = new User();

        secondUser.setName("Elmo");

        newUserList.add(secondUser);

        assertEquals(1, newUserList.size());

        secondUser.setName("Oscar");

        newUserList.add(secondUser);

        assertEquals(2, newUserList.size());

        newUserList.remove(secondUser);

        assertEquals(1, newUserList.size());
    }

}
