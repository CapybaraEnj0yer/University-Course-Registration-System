package com.university.repository;

import com.university.model.*;
import java.util.HashMap;
import java.util.Map;

public class InMemoryUserRepository implements UserRepository {
    private Map<String, User> database = new HashMap<>();

    public InMemoryUserRepository() {
        // Admins
        save(new Admin("admin", "admin", "System Admin"));

        // Professors
        save(new Professor("P01", "P01", "Dr. Alan Turing"));
        save(new Professor("P02", "P02", "Dr. Ada Lovelace"));

        // Students
        save(new Student("S01", "S01", "Alice Smith"));
        save(new Student("S02", "S02", "Bob Jones"));
        save(new Student("S03", "S03", "Charlie Brown"));
    }

    @Override
    public User findByUsername(String username) {
        return database.get(username);
    }

    @Override
    public void save(User user) {
        database.put(user.getUsername(), user);
    }

    @Override
    public java.util.List<User> findAll() {
        return new java.util.ArrayList<>(database.values());
    }
}