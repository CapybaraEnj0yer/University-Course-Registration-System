package com.university.model;

public class Admin extends User {

    public Admin(String id, String username, String name) {
        super(id, username, name);
    }

    @Override
    public String getRole() {
        return "ADMIN";
    }
}