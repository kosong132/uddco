package com.uddco.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.google.api.core.ApiFuture;
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
            throw new IllegalArgumentException("Username already taken");
        }

        if (emailExists(user.getEmail())) {
            System.out.println("Duplicate email: " + user.getEmail()); // Console log
            throw new IllegalArgumentException("Email already in use");
        }

        // Encode password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Set default role as "Seller" if not provided by frontend
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("Seller");
            user.setUserLevel(1); // Seller = 1
        } else {
            // Set userLevel based on role from frontend
            switch (user.getRole()) {
                case "Customer":
                    user.setUserLevel(2);
                    break;
                case "Seller":
                default:
                    user.setUserLevel(1);
                    break;
            }
        }

        if (user.getUserLevel() == 0) {
            user.setUserLevel(2); // 2 = Customer
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

    public String requestResetPassword(String email, boolean isMobile) throws ExecutionException, InterruptedException {
        email = email.trim();

        // Fetch user from Firestore based on the email
        QuerySnapshot querySnapshot = firestore.collection("users")
                .whereEqualTo("email", email)
                .get()
                .get();

        List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();
        if (documents.isEmpty()) {
            return "Email does not exist!";
        }

        String userId = documents.get(0).getId();
        String token;
        String message;

        if (isMobile) {
            // Generate a 6-digit OTP for mobile
            token = String.format("%06d", new Random().nextInt(999999));
            message = "Your UDD.Co password reset OTP is: " + token;
        } else {
            // Generate UUID for web and create a password reset link
            token = UUID.randomUUID().toString();
            String resetLink = "http://localhost:3000/reset-new-password?token=" + token;
            message = "<h1>Reset Your Password</h1>"
                    + "<p>Click the link below to reset your password:</p>"
                    + "<p><a href=\"" + resetLink + "\">Reset Password</a></p>"
                    + "<p>If you did not request this, please ignore this email.</p>";
        }

        // Save the token to Firestore
        Map<String, Object> tokenData = new HashMap<>();
        tokenData.put("userId", userId);
        tokenData.put("token", token);
        tokenData.put("email", email);
        tokenData.put("createdAt", System.currentTimeMillis());

        firestore.collection("reset_tokens").document(token).set(tokenData).get();

        // Send the email with the reset link or OTP
        emailUtil.sendResetPasswordEmail(email, message, isMobile);

        return isMobile ? "OTP sent to your email!" : "Reset password link sent to your email!";
    }

    public String requestResetOtp(String email) throws ExecutionException, InterruptedException {
        email = email.trim();

        // Fetch user from Firestore based on the email
        QuerySnapshot querySnapshot = firestore.collection("users")
                .whereEqualTo("email", email)
                .get()
                .get();

        List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();
        if (documents.isEmpty()) {
            return "Email does not exist!";
        }

        String userId = documents.get(0).getId();

        // Generate 6-digit OTP for mobile
        String otp = String.format("%06d", new Random().nextInt(999999));

        // Save the OTP to Firestore
        Map<String, Object> tokenData = new HashMap<>();
        tokenData.put("userId", userId);
        tokenData.put("token", otp);
        tokenData.put("email", email);
        tokenData.put("createdAt", System.currentTimeMillis());

        firestore.collection("reset_tokens").document(otp).set(tokenData).get();

        // Send the OTP to the user's email
        emailUtil.sendResetPasswordEmail(email, "Your OTP for resetting the password is: " + otp, true);

        return "OTP sent to your email!";
    }

    // public String resetPassword(String token, String newPassword) throws ExecutionException, InterruptedException {
    //     // Check if the token exists in Firestore
    //     DocumentSnapshot snapshot = firestore.collection("reset_tokens").document(token).get().get();
    //     if (!snapshot.exists()) {
    //         throw new RuntimeException("Invalid or expired token!");
    //     }
    //     // Get userId from the token document
    //     String userId = snapshot.getString("userId");
    //     // Update the password in the users collection
    //     firestore.collection("users").document(userId)
    //             .update("password", passwordEncoder.encode(newPassword))
    //             .get();
    //     // Optionally delete the token after successful password reset
    //     firestore.collection("reset_tokens").document(token).delete();
    //     return "Password reset successfully!";
    // }
    public String resetPassword(String otp, String newPassword) throws ExecutionException, InterruptedException {
        DocumentSnapshot snapshot = firestore.collection("reset_tokens").document(otp).get().get();

        if (!snapshot.exists()) {
            throw new RuntimeException("Invalid or expired OTP!");
        }

        long createdAt = snapshot.getLong("createdAt");
        long currentTime = System.currentTimeMillis();
        long expiryTime = 10 * 60 * 1000; // Example: OTP expires in 10 minutes

        if (currentTime - createdAt > expiryTime) {
            throw new RuntimeException("OTP has expired!");
        }

        // Proceed with resetting the password
        String userId = snapshot.getString("userId");

        // Update the password
        firestore.collection("users").document(userId)
                .update("password", passwordEncoder.encode(newPassword))
                .get();

        // Optionally delete the token after use
        firestore.collection("reset_tokens").document(otp).delete();

        return "Password reset successfully!";
    }

    public String resetPasswordWithOtp(String otp, String newPassword) throws ExecutionException, InterruptedException {
        if (otp == null || otp.trim().isEmpty()) {
            throw new RuntimeException("OTP cannot be empty!");
        }

        // Trim and log the received data
        otp = otp.trim();
        System.out.println("✅ [BACKEND] Received OTP: " + otp);
        System.out.println("✅ [BACKEND] Received new password: " + newPassword);

        // Try to find the document where "token" == otp
        ApiFuture<QuerySnapshot> query = firestore.collection("reset_tokens")
                .whereEqualTo("token", otp)
                .get();

        List<QueryDocumentSnapshot> documents = query.get().getDocuments();

        if (documents.isEmpty()) {
            System.out.println("❌ No token document found for OTP: " + otp);
            throw new RuntimeException("Invalid or expired OTP!");
        }

        DocumentSnapshot snapshot = documents.get(0);
        String docId = snapshot.getId(); // For deletion

        Long createdAt = snapshot.getLong("createdAt");
        if (createdAt == null) {
            throw new RuntimeException("Invalid token data: no timestamp found.");
        }

        long currentTime = System.currentTimeMillis();
        long expiryTime = 10 * 60 * 1000; // 10 minutes

        if (currentTime - createdAt > expiryTime) {
            throw new RuntimeException("OTP has expired!");
        }

        String userId = snapshot.getString("userId");
        if (userId == null) {
            throw new RuntimeException("Invalid token data: no user ID associated.");
        }

        firestore.collection("users").document(userId)
                .update("password", passwordEncoder.encode(newPassword))
                .get();

        firestore.collection("reset_tokens").document(docId).delete();

        System.out.println("✅ Password reset successful for user: " + userId);
        return "Password reset successfully!";
    }

}
