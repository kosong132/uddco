package com.uddco.service;

import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.uddco.model.User;
@Service
public class AuthService {

    @Autowired
    private Firestore firestore;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public String register(User user) throws ExecutionException, InterruptedException {
        System.out.println("Registering user: " + user.getEmail());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        firestore.collection("users").document(user.getUserId()).set(user).get();
        return "User registered successfully!";
    }

    public User login(String email, String password) throws ExecutionException, InterruptedException {
        System.out.println("Logging in user: " + email);
        QuerySnapshot querySnapshot = firestore.collection("users")
                .whereEqualTo("email", email)
                .get()
                .get();

        List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();
        if (documents.isEmpty()) {
            throw new RuntimeException("User not found!");
        }

        User user = documents.get(0).toObject(User.class);
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid password!");
        }

        return user;
    }

    public String resetPassword(String email, String newPassword) throws ExecutionException, InterruptedException {
        System.out.println("Resetting password for user: " + email);
        QuerySnapshot querySnapshot = firestore.collection("users")
                .whereEqualTo("email", email)
                .get()
                .get();

        List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();
        if (documents.isEmpty()) {
            throw new RuntimeException("User not found!");
        }

        String userId = documents.get(0).getId();
        firestore.collection("users").document(userId)
                .update("password", passwordEncoder.encode(newPassword))
                .get();

        return "Password reset successfully!";
    }
}