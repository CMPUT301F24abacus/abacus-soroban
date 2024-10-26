package com.example.soroban;


/**
 * Any class that deals with the modification of a User object must utilize this class.
 * @Author: Matthieu Larochelle
 * @Version: 1.0
 */
public class UserController {
    private User user;

    /**
     * Constructor method for UserController.
     * @Author: Matthieu Larochelle
     * @Version: 1.0
     */
    public UserController(User user){
        this.user = user;
    }

    /**
     * Update User.
     * @Author: Matthieu Larochelle
     * @Version: 1.0
     * @param firstName: User's new first name.
     * @param lastName : User's new last name.
     * @param email: User's new email address.
     * @param number: User's new phone number.
     */
    public void updateUser(String firstName, String lastName, String email, String number){
        user.setName(firstName + lastName);
        user.setEmail(email);
        user.setPhoneNumber(Integer.parseInt(number));
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
