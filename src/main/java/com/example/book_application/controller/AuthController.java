package com.example.book_application.controller;

import com.example.book_application.config.JwtService;
import com.example.book_application.dto.AuthRequest;
import com.example.book_application.dto.AuthResponse;
import com.example.book_application.model.User;
import com.example.book_application.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody User user) {
        try {
            User savedUser = userService.saveUser(user);
            UserDetails userDetails = userService.loadUserByUsername(savedUser.getUsername());
            String token = jwtService.generateToken(userDetails);
            return ResponseEntity.ok()
                .body(new AuthResponse(token));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
            
            UserDetails userDetails = userService.loadUserByUsername(request.getUsername());
            String token = jwtService.generateToken(userDetails);
            return ResponseEntity.ok()
                .body(new AuthResponse(token));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
} 