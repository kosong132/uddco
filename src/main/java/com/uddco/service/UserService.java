// package com.uddco.service;

// import java.util.concurrent.ExecutionException;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;

// import com.google.cloud.firestore.DocumentReference;
// import com.google.cloud.firestore.Firestore;
// import com.uddco.model.User;

// @Service
// public class UserService {

//     @Autowired
//     private Firestore firestore;

//     public String addUser(User user) throws ExecutionException, InterruptedException {
//         // Add user to Firestore and auto-generate ID
//         DocumentReference addedUserRef = firestore.collection("users").document();
//         user.setId(addedUserRef.getId()); // Set the auto-generated ID
//         addedUserRef.set(user).get(); // .get() blocks until the operation is complete
//         return addedUserRef.getId();
//     }
// }