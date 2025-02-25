package com.example.book_application.service;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.book_application.core.excepiton.ResourceNotFoundException;
import com.example.book_application.model.User;
import com.example.book_application.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
@Service
@Slf4j
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    public User saveUser(User user) {
        log.info("Creating new user with email: {}", user.getEmail());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Cacheable(value = "users", key = "#email", unless = "#result == null")
    public User findByEmail(String email) {
        log.info("Fetching user from database by email: {}", email);
        return userRepository.findByEmail(email)
            .orElseThrow(() -> {
                log.warn("User not found with email: {}", email);
                return new ResourceNotFoundException("User not found with email: " + email);
            });
    }

    @Override
    @Cacheable(value = "userDetails", key = "#email", unless = "#result == null")
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info("Loading user details from database: {}", email);
        return userRepository.findByEmail(email)
            .map(user -> {
                log.debug("User found: {}", user.getEmail());
                return new org.springframework.security.core.userdetails.User(
                    user.getEmail(),
                    user.getPassword(),
                    Collections.emptyList()
                );
            })
            .orElseThrow(() -> {
                log.warn("Authentication failed - user not found: {}", email);
                return new UsernameNotFoundException("User not found with email: " + email);
            });
    }

    public List<User> findAllUsers() {
        log.info("Fetching all users");
        return userRepository.findAll();
    }

    public User findById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    public User updateUser(Long id, User userDetails) { 
        User user = findById(id);
        user.setEmail(userDetails.getEmail());
        user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        return userRepository.save(user);
    }   

    public void deleteUser(Long id) {
        User user = findById(id);
        userRepository.delete(user);
    }

    public User login(String username, String password) {
        log.info("Attempting login for user: {}", username);
        return userRepository.findByUsername(username)
            .filter(user -> passwordEncoder.matches(password, user.getPassword()))
            .orElseThrow(() -> {
                log.warn("Login failed for user: {}", username);
                return new RuntimeException("Invalid credentials");
            });
    }
}