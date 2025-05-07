package com.uddco.model;

public class User {
    private String userId;       // Unique user ID (Primary Key)
    private String username;     // User's username
    private String password;     // User's password
    private String email;        // User's email address
    private int phoneNumber;     // User's phone number
    private String role;         // User's role in the system (e.g., "User")
    private int userLevel;       // 1 = Seller, 2 = Customer
    private String token;        // JWT token for authenticated sessions

    // Constructors
    public User() {}

    public User(String userId, String username, String password, String email, int phoneNumber, String role, int userLevel, String token) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.role = role;
        this.userLevel = userLevel;
        this.token = token;
    }

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(int phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getUserLevel() {
        return userLevel;
    }

    public void setUserLevel(int userLevel) {
        this.userLevel = userLevel;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
