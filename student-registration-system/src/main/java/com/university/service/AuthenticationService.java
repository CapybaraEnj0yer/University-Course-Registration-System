package com.university.service;

import com.university.model.User;
import com.university.repository.UserRepository;

public class AuthenticationService {
    private UserRepository userRepository;

    public AuthenticationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Now it only requires a username
    public User login(String username) {
        return userRepository.findByUsername(username);
    }
}