package com.uddco.service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

    // Check if email exists and send reset password link
public String requestResetPassword(String email) throws ExecutionException, InterruptedException {
    // Trim the email to remove leading/trailing whitespace
    email = email.trim();

    System.out.println("Querying Firestore for email: " + email);

    // Find user by email
    QuerySnapshot querySnapshot = firestore.collection("users")
            .whereEqualTo("email", email)
            .get()
            .get();

    List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();
    System.out.println("Number of documents found: " + documents.size());

    if (documents.isEmpty()) {
        System.out.println("No documents found for email: " + email);
        return "Email does not exist!";
    }

    // Log the first document (for debugging)
    QueryDocumentSnapshot document = documents.get(0);
    System.out.println("Document data: " + document.getData());

    // Generate a unique token (for simplicity, use UUID)
    String token = UUID.randomUUID().toString();

    // Send reset password link via email
    String resetLink = "http://your-frontend-url/reset-password?token=" + token;
    emailUtil.sendResetPasswordEmail(email, resetLink);

    return "Reset password link sent to your email!";
}
    // Reset password
    public String resetPassword(String email, String newPassword) throws ExecutionException, InterruptedException {
        // Find user by email
        QuerySnapshot querySnapshot = firestore.collection("users")
                .whereEqualTo("email", email)
                .get()
                .get();

        List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();
        if (documents.isEmpty()) {
            throw new RuntimeException("User not found!");
        }

        // Update the password
        String userId = documents.get(0).getId();
        firestore.collection("users").document(userId)
                .update("password", passwordEncoder.encode(newPassword))
                .get();

        return "Password reset successfully!";
    }

}
