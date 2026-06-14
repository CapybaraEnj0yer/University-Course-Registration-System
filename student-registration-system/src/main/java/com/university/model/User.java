package com.university.model;

public abstract class User {
    private String id;
    private String username;
    private String password; // In a real app, store a hash, not plain text!
    private String name;

    public User(String id, String username, String name) {
        this.id = id;
        this.username = username;
        this.name = name;
    }

    // Standard Getters and Setters
    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    // Every subclass must define its role
    public abstract String getRole();
}