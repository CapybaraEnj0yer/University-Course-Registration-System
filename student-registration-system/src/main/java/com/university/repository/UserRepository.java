package com.university.repository;

import com.university.model.User;

public interface UserRepository {
    User findByUsername(String username);

    void save(User user);

    java.util.List<User> findAll();
}