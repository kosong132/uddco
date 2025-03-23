// package com.uddco.controller;

// import java.util.concurrent.ExecutionException;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;

// import com.uddco.model.User;
// import com.uddco.service.UserService;

// @RestController
// @RequestMapping("/users")
// public class UserController {

//     @Autowired
//     private UserService userService;

//     @PostMapping
//     public String addUser(@RequestBody User user) {
//         try {
//             return userService.addUser(user);
//         } catch (InterruptedException | ExecutionException e) {
//             return "Error adding user: " + e.getMessage();
//         }
//     }
// }