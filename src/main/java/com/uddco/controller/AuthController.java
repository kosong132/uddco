package com.uddco.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.uddco.model.User;
import com.uddco.service.AuthService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public String register(@RequestBody User user) {
        try {
            return authService.register(user);
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    @PostMapping("/login")
    public User login(@RequestParam String email, @RequestParam String password) {
        try {
            return authService.login(email, password);
        } catch (Exception e) {
            throw new RuntimeException("Login failed: " + e.getMessage());
        }
    }
 @PostMapping("/request-reset-password")
    public String requestResetPassword(@RequestParam String email) {
        try {
            return authService.requestResetPassword(email);
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam String email, @RequestParam String newPassword) {
        try {
            return authService.resetPassword(email, newPassword);
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
}