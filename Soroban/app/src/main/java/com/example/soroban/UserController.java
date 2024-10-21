package com.example.soroban;

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
     * Update a user's name.
     * @Author: Matthieu Larochelle
     * @Version: 1.0
     * @param name: New User name.
     */
    public void updateName(String name){
        user.setName(name);
    }

    /**
     * Update a user's email.
     * @Author: Matthieu Larochelle
     * @Version: 1.0
     * @param email: New User email.
     */
    public void updateEmail(String email){
        user.setEmail(email);
    }

    /**
     * Update a user's phone number.
     * @Author: Matthieu Larochelle
     * @Version: 1.0
     * @param phoneNumber: New User phone number.
     */
    public void updatePhoneNumber(String phoneNumber){
        int number = Integer.parseInt(phoneNumber);
        user.setPhoneNumber(number);
    }
}
