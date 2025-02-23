package com.example.book_application.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import com.example.book_application.model.User;
import com.example.book_application.service.UserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public List<User> getAllUsers() {
        log.info("Fetching all users");
        return userService.findAllUsers();
    }

    @PostMapping
    public User createUser( @RequestBody User user) {
        log.info("Creating user: {}", user.getEmail());
        return userService.saveUser(user);
    }

    @GetMapping("/{email}")
    public User getUserByEmail(@PathVariable String email) {
        log.info("Fetching user by email: {}", email);
        return userService.findByEmail(email);
    }

}