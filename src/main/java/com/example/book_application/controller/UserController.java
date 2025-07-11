package com.example.book_application.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.List;

import com.example.book_application.model.User;
import com.example.book_application.service.UserService;
import com.example.book_application.dto.LoginRequest;
import com.example.book_application.dto.LoginResponse;
import com.example.book_application.dto.UserProfileResponse;
import com.example.book_application.dto.UserProfileUpdateRequest;

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
    public User createUser(@RequestBody User user) {
        log.info("Creating user: {}", user.getEmail());
        return userService.saveUser(user);
    }

    @GetMapping("/email/{email}")
    public User getUserByEmail(@PathVariable String email) {
        log.info("Fetching user by email: {}", email);
        return userService.findByEmail(email);
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        log.info("Fetching user with ID: {}", id);
        return userService.findById(id);
    }

    @PutMapping("/{id}")
    public User updateUser(@PathVariable Long id, @RequestBody User user) {
        log.info("Updating user with ID: {}", id);
        return userService.updateUser(id, user);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        log.info("Deleting user with ID: {}", id);
        userService.deleteUser(id);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            User user = userService.login(loginRequest.getUsername(), loginRequest.getPassword());
            LoginResponse response = new LoginResponse(user.getId(), user.getUsername(), user.getEmail());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid credentials");
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getUserProfile(Authentication authentication) {
        log.info("Profil bilgileri isteniyor: {}", authentication.getName());
        UserProfileResponse profile = userService.getUserProfile(authentication.getName());
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/profile")
    public ResponseEntity<UserProfileResponse> updateUserProfile(
            Authentication authentication,
            @RequestBody UserProfileUpdateRequest request) {
        log.info("Profil güncelleme isteği: {}", authentication.getName());
        
        try {
            UserProfileResponse updatedProfile = userService.updateUserProfile(
                authentication.getName(), 
                request
            );
            return ResponseEntity.ok(updatedProfile);
        } catch (RuntimeException e) {
            log.error("Profil güncellenirken hata: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}