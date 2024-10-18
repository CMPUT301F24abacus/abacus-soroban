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
     * @return: Result if User was successfully added to list of users.
     */
    public Boolean add(User user){
        if(userList.contains(user)){
            return Boolean.FALSE; // User already present in list of users.
        }else{
            return userList.add(user);
        }
    }

    /**
     * Remove an event from an event list.
     * @Author: Matthieu Larochelle
     * @Version: 1.0
     * @Param: Event.
     * @return: Result if User was successfully removed from list of users.
     */
    public Boolean remove(User user){
        if(userList.contains(user)){
            return userList.remove(user);
        }else{
            return Boolean.FALSE; // Event is not present in list of users.;
        }
    }

    /**
     * Return the number of users in the user list.
     * @Author: Matthieu Larochelle
     * @Version: 1.0
     * @return: number of users integer.
     */
    public Integer size(){
        return userList.size();
    }
}

