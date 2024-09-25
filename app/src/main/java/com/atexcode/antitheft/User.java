package com.atexcode.antitheft;

public class User {
    private String userId;
    private String name;
    private String email;
    private String phone;
    private String address;

    public User() {
        // Default constructor required for Firebase Realtime Database
    }

    public User(String userId, String name, String email, String phone, String address) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
    }

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }
}
