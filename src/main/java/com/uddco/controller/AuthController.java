package com.uddco.controller;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.uddco.model.User;
import com.uddco.service.AuthService;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:3000")

public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User user) {
        try {
            // Set default role and generate userId
            user.setRole("User");
            user.setUserId(UUID.randomUUID().toString());
            return ResponseEntity.ok(authService.register(user));  // Successful registration
        } catch (Exception e) {
            // Return the error message with a specific HTTP status code
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user, @RequestParam(value = "client", required = false) String client) {
        try {
            User authenticatedUser = authService.login(user.getUsername(), user.getPassword());

            // If request is from web, set cookie (e.g., client=web)
            if ("web".equalsIgnoreCase(client)) {
                String token = UUID.randomUUID().toString(); // You can use JWT later

                ResponseCookie cookie = ResponseCookie.from("auth_token", token)
                        .httpOnly(true)
                        .secure(false) // Change to true in production (HTTPS)
                        .path("/")
                        .maxAge(7 * 24 * 60 * 60) // 7 days
                        .sameSite("Lax")
                        .build();

                return ResponseEntity.ok()
                        .header("Set-Cookie", cookie.toString())
                        .body(authenticatedUser);
            }

            // For mobile or other clients, return user data only
            return ResponseEntity.ok(authenticatedUser);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Login failed: " + e.getMessage());
        }
    }

    // @PostMapping("/login")
    // public User login(@RequestBody User user) {
    //     try {
    //         return authService.login(user.getUsername(), user.getPassword());
    //     } catch (Exception e) {
    //         throw new RuntimeException("Login failed: " + e.getMessage());
    //     }
    // }
    @PostMapping("/request-reset-password")
    public String requestResetPassword(@RequestParam String email,
            @RequestParam(defaultValue = "false") boolean isMobile) {
        try {
            return authService.requestResetPassword(email, isMobile);
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    @PostMapping("/request-reset-password/mobile")
    public ResponseEntity<String> requestMobileResetOtp(@RequestParam String email) {
        try {
            String result = authService.requestResetOtp(email);  // This will call the new OTP method
            return ResponseEntity.ok(result); // ✅ Success
        } catch (Exception e) {
            e.printStackTrace(); // Log for debugging
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Server error: " + e.getMessage()); // Return detailed error message
        }
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam String token, @RequestParam String newPassword) {
        try {
            return authService.resetPassword(token, newPassword);
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    @PostMapping("/reset-password/mobile")
    public ResponseEntity<String> resetPasswordMobile(@RequestParam String otp, @RequestParam String newPassword) {
        try {
            // Trim input to avoid issues with leading/trailing spaces
            String trimmedOtp = otp.trim();
            // Call service to reset password with OTP
            String result = authService.resetPasswordWithOtp(trimmedOtp, newPassword);
            return ResponseEntity.ok(result); // ✅ Success
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error: " + e.getMessage()); // ✅ Error with message
        }
    }
        // GET /auth/users/{userId}
    @GetMapping("/users/{userId}")
    public User getUserById(@PathVariable String userId) throws ExecutionException, InterruptedException {
        return authService.getUserById(userId);
    }


}
