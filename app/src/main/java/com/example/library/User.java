package com.example.library;

public class User {
    public String username;
    public String email;
    public boolean isAdmin;


    public User(String username, String email, boolean isAdmin) {
        this.username = username;
        this.email = email;
        this.isAdmin = isAdmin;

    }

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

}
