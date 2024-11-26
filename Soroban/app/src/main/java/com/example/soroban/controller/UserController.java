package com.example.soroban.controller;


import com.example.soroban.FireBaseController;
import com.example.soroban.model.Event;
import com.example.soroban.model.User;

/**
 * Any class that deals with the modification of a User object must utilize this class.
 * Provides methods for updating user details and hosted events.
 * @Author: Matthieu Larochelle
 * @Version: 1.1
 * @see User
 * @see Event
 */
public class UserController {
    private User user;

    /**
     * Constructor method for UserController.
     * @Author: Matthieu Larochelle
     * @Version: 1.0
     * @param user the {@link User} to manage.
     */
    public UserController(User user){
        this.user = user;
    }

    /**
     * Update User.
     * @Author: Matthieu Larochelle
     * @Version: 1.1
     * @param firstName: User's new first name.
     * @param lastName : User's new last name.
     * @param email: User's new email address.
     * @param number: User's new phone number.
     */
    public void updateUser(CharSequence firstName, CharSequence lastName, CharSequence email, CharSequence number){
        user.setFirstName(firstName.toString());
        user.setLastName(lastName.toString());
        user.setEmail(email.toString());
        long phoneNumber = Long.parseLong(number.toString().replaceAll("\\D", ""));
        user.setPhoneNumber(phoneNumber);
    }

    /**
     * Adds an event to the list of hosted events for the {@link User}.
     *
     * @param event the {@link Event} to add.
     * @return {@code true} if the addition was successful, {@code false} otherwise.
     */
    public boolean addHostedEvent(Event event){
        return user.addHostedEvent(event);
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
