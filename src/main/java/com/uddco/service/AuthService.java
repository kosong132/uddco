package com.uddco.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.uddco.model.User;
import com.uddco.util.EmailUtil;

@Service
public class AuthService {

    @Autowired
    private Firestore firestore;
    @Autowired
    private EmailUtil emailUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // public String register(User user) throws ExecutionException, InterruptedException {
    //     System.out.println("Registering user: " + user.getEmail());
    //     user.setPassword(passwordEncoder.encode(user.getPassword()));
    //     firestore.collection("users").document(user.getUserId()).set(user).get();
    //     return "User registered successfully!";
    // }
    public String register(User user) throws ExecutionException, InterruptedException {
        System.out.println("Registering user: " + user.getEmail());
  // Check for duplicate username or email
    if (usernameExists(user.getUsername())) {
        System.out.println("Duplicate username: " + user.getUsername()); // Console log
        throw new RuntimeException("Username already taken");
    }

    if (emailExists(user.getEmail())) {
        System.out.println("Duplicate email: " + user.getEmail()); // Console log
        throw new RuntimeException("Email already in use");
    }
        // Encode password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Set role and generate userId if not provided
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("User");
        }
        if (user.getUserId() == null || user.getUserId().isEmpty()) {
            user.setUserId(UUID.randomUUID().toString());
        }

        // Save to Firestore
        firestore.collection("users").document(user.getUserId()).set(user).get();
        System.out.println("User saved to Firestore with ID: " + user.getUserId());
        return "User registered successfully!";
    }

    public boolean usernameExists(String username) throws ExecutionException, InterruptedException {
        QuerySnapshot querySnapshot = firestore.collection("users")
                .whereEqualTo("username", username)
                .get()
                .get();

        boolean exists = !querySnapshot.getDocuments().isEmpty();
        System.out.println("Checking username: " + username + " | Exists: " + exists); // Console log
        return exists;
    }

    public boolean emailExists(String email) throws ExecutionException, InterruptedException {
        QuerySnapshot querySnapshot = firestore.collection("users")
                .whereEqualTo("email", email)
                .get()
                .get();

        boolean exists = !querySnapshot.getDocuments().isEmpty();
        System.out.println("Checking email: " + email + " | Exists: " + exists); // Console log
        return exists;
    }

    public User login(String username, String password) throws ExecutionException, InterruptedException {
        System.out.println("Logging in user: " + username);
        QuerySnapshot querySnapshot = firestore.collection("users")
                .whereEqualTo("username", username)
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

    public String requestResetPassword(String email) throws ExecutionException, InterruptedException {
        email = email.trim();

        QuerySnapshot querySnapshot = firestore.collection("users")
                .whereEqualTo("email", email)
                .get()
                .get();

        List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();
        if (documents.isEmpty()) {
            return "Email does not exist!";
        }

        // Generate token
        String token = UUID.randomUUID().toString();
        String userId = documents.get(0).getId();

        // Save token to Firestore (could use a "password_resets" collection)
        Map<String, Object> tokenData = new HashMap<>();
        tokenData.put("userId", userId);
        tokenData.put("token", token);
        tokenData.put("email", email);
        tokenData.put("createdAt", System.currentTimeMillis());

        firestore.collection("reset_tokens").document(token).set(tokenData).get();

        // Send link with token
        String resetLink = "http://localhost:3000/reset-new-password?token=" + token;
        emailUtil.sendResetPasswordEmail(email, resetLink);

        return "Reset password link sent to your email!";
    }

    public String resetPassword(String token, String newPassword) throws ExecutionException, InterruptedException {
        DocumentSnapshot snapshot = firestore.collection("reset_tokens").document(token).get().get();
        if (!snapshot.exists()) {
            throw new RuntimeException("Invalid or expired token!");
        }

        String userId = snapshot.getString("userId");

        // Update the password
        firestore.collection("users").document(userId)
                .update("password", passwordEncoder.encode(newPassword))
                .get();

        // Optionally delete the token after use
        firestore.collection("reset_tokens").document(token).delete();

        return "Password reset successfully!";
    }

}
