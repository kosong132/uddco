// package com.uddco.controller;

// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.web.bind.annotation.RestController;

// import com.google.firebase.auth.FirebaseAuth;
// import com.google.firebase.auth.FirebaseAuthException;
// import com.google.firebase.auth.UserRecord;

// @RestController
// public class FirebaseTestController {

//     @GetMapping("/firebase/test")
//     public String testFirebase(@RequestParam String email) {
//         try {
//             UserRecord userRecord = FirebaseAuth.getInstance().getUserByEmail(email);
//             return "Firebase user found: " + userRecord.getEmail();
//         } catch (FirebaseAuthException e) {
//             return "User not found: " + e.getMessage();
//         }
//     }
// }
