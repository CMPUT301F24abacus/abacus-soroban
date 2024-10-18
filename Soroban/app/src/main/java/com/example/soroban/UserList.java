package com.example.soroban;

import java.util.ArrayList;


/**
 * Keeps track of user objects.
 * @Author: Matthieu Larochelle
 * @Version: 1.1
 */
public class UserList {
    private ArrayList<User> userList;

    /**
     * Constructor method for UserList.
     * @Author: Matthieu Larochelle
     * @Version: 1.0
     */
    public UserList(){
        this.userList = new ArrayList<User>();
    }

    /**
     * Add a user to a user list.
     * @Author: Matthieu Larochelle
     * @Version: 1.0
     * @Param: User.
     */
    public void add(User user){
        if(userList.contains(user)){
            throw new IllegalArgumentException("User already present in list of users.");
        }else{
            userList.add(user);
        }
    }

    /**
     * Remove an event from an event list.
     * @Author: Matthieu Larochelle
     * @Version: 1.0
     * @Param: Event.
     */
    public void remove(User user){
        if(userList.contains(user)){
            userList.remove(user);
        }else{
            throw new IllegalArgumentException("Event is not present in list of users..");
        }
    }
}

