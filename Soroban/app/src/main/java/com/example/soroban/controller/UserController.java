package com.example.soroban.controller;


import com.example.soroban.FireBaseController;
import com.example.soroban.model.User;

/**
 * Any class that deals with the modification of a User object must utilize this class.
 * @Author: Matthieu Larochelle
 * @Version: 1.1
 */
public class UserController {
    private User user;
    private FireBaseController fireBaseController;

    /**
     * Constructor method for UserController.
     * @Author: Matthieu Larochelle
     * @Version: 1.1
     */
    public UserController(User user){
        this.user = user;
        this.fireBaseController = new FireBaseController();
    }

    /**
     * Update User.
     * @Author: Matthieu Larochelle
     * @Version: 1.2
     * @param firstName: User's new first name.
     * @param lastName : User's new last name.
     * @param email: User's new email address.
     * @param number: User's new phone number.
     */
    public void updateUser(CharSequence firstName, CharSequence lastName, CharSequence email, CharSequence number){
        user.setFirstName(firstName.toString());
        user.setLastName(lastName.toString());
        user.setEmail(email.toString());
        int phoneNumber = Integer.parseInt(number.toString().replaceAll("\\D", ""));
        user.setPhoneNumber(phoneNumber);
        fireBaseController.userUpdate(user);
    }

    /**
     * Remove User and any owned Events/Facility from associated Users, Events, UserList and EventLists.
     * @Author: Matthieu Larochelle
     * @Version: 1.0
     */
    public void removeUser(){
        user.destroy();
    }

}
