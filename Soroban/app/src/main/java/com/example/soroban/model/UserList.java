package com.example.soroban.model;

import java.io.Serializable;
import java.util.ArrayList;


/**
 * Keeps track of user objects.
 * @Author: Matthieu Larochelle
 * @Version: 2.0
 */
public class UserList extends ArrayList<User> implements Serializable {
    /**
     * Constructor method for UserList.
     * @Author: Matthieu Larochelle
     * @Version: 2.0
     */
    public UserList(){
        super();
    }

    /**
     * Destructor method for UserList.
     * @Author: Matthieu Larochelle
     * @Version: 1.0
     */
    public void destroy(){
        for(int i = 0; i < this.size(); i++){
            this.get(i).destroy();
        }
    }

    /**
     * Add user to UserList.
     * @Author: Matthieu Larochelle
     * @Version: 1.0
     * @return: Result of addition of User to User List
     */
    public boolean addUser(User user){
        if(this.contains(user)){
            return false;
        }else{
            return add(user);
        }
    }


    /**
     * Find event in UserList.
     * @Author: Matthieu Larochelle
     * @Version: 1.0
     * @Return: Null if no matching user was found; the matching user otherwise
     */
    public User find(User user){
        for(int i = 0; i < this.size(); i++){
            if(this.get(i).equals(user)){
                return user;
            }
        }
        return null;
    }

}

