package com.example.soroban.controller;

import com.example.soroban.model.User;
import com.example.soroban.model.UserList;

/**
 * Any class that deals with the modification of a UserList object must utilize this class.
 * @Author: Matthieu Larochelle
 * @Version: 1.0
 */
public class UserListController {
    private UserList userList;

    /**
     * Constructor method for UserListController.
     * @Author: Matthieu Larochelle
     * @Version: 1.0
     */
    public UserListController(UserList userList){
        this.userList = userList;
    }

    /**
     * Update User in UserList.
     * @Author: Matthieu Larochelle
     * @Version: 1.0
     * @param user : Target User.
     * @param firstName: User's new first name.
     * @param lastName : User's new last name.
     * @param email: User's new email address.
     * @param number: User's new phone number.
     * @return : Result of successful update of User in UserList.
     */
    public Boolean updateUser(User user, String firstName, String lastName, String email, String number){
        if(userList.contains(user)){
            user.setFirstName(firstName + lastName);
            user.setEmail(email);
            user.setPhoneNumber(Integer.parseInt(number));
            return Boolean.TRUE;
        }else{
            return Boolean.FALSE;
        }
    }

    /**
     * Add User to UserList
     * @Author: Matthieu Larochelle
     * @version : 1.0
     * @param user
     * @return : Result of successful addition of User to UserList.
     */
    public Boolean addUser(User user){
        return userList.add(user);
    }

    /**
     * Remove User to UserList
     * @Author: Matthieu Larochelle
     * @version : 1.0
     * @param user
     * @return : Result of successful removal of User from UserList.
     */
    public Boolean removeUser(User user){
        return userList.remove(user);
    }

}
