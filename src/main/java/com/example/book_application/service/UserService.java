package com.example.book_application.service;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.book_application.core.excepiton.ResourceNotFoundException;
import com.example.book_application.dto.UserProfileResponse;
import com.example.book_application.dto.UserProfileUpdateRequest;
import com.example.book_application.model.User;
import com.example.book_application.repository.UserRepository;
import com.example.book_application.repository.BookProgressRepository;
import com.example.book_application.repository.SavedWordRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final BookProgressRepository bookProgressRepository;
    private final SavedWordRepository savedWordRepository;

    public User saveUser(User user) {
        log.info("Yeni kullanıcı kaydediliyor: {}", user.getUsername());
        
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            log.error("Bu kullanıcı adı zaten kullanımda: {}", user.getUsername());
            throw new RuntimeException("Bu kullanıcı adı zaten kullanımda");
        }
        
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            log.error("Bu email zaten kullanımda: {}", user.getEmail());
            throw new RuntimeException("Bu email zaten kullanımda");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);
        log.info("Kullanıcı başarıyla kaydedildi: {}", savedUser.getUsername());
        
        return savedUser;
    }

    public User login(String username, String password) {
        log.info("Kullanıcı girişi deneniyor: {}", username);
        
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> {
                log.error("Kullanıcı bulunamadı: {}", username);
                return new RuntimeException("Geçersiz kullanıcı adı veya şifre");
            });

        if (!passwordEncoder.matches(password, user.getPassword())) {
            log.error("Şifre eşleşmedi: {}", username);
            throw new RuntimeException("Geçersiz kullanıcı adı veya şifre");
        }

        log.info("Kullanıcı başarıyla giriş yaptı: {}", username);
        return user;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Kullanıcı yükleniyor: {}", username);
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("Kullanıcı bulunamadı: {}", username);
                    return new UsernameNotFoundException("Kullanıcı bulunamadı: " + username);
                });

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Kullanıcı bulunamadı: " + username));
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Email ile kullanıcı bulunamadı: " + email));
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public User findById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    public User updateUser(Long id, User userDetails) { 
        User user = findById(id);
        user.setEmail(userDetails.getEmail());
        if (userDetails.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        }
        return userRepository.save(user);
    }   

    public void deleteUser(Long id) {
        User user = findById(id);
        userRepository.delete(user);
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return findByUsername(username);
    }

    public UserProfileResponse getUserProfile(String username) {
        User user = findByUsername(username);
        
        // İstatistikleri hesapla
        Integer totalBooks = bookProgressRepository.countDistinctBooksByUserId(user.getId());
        Integer totalSavedWords = savedWordRepository.countByUserId(user.getId());
        Long totalReadingTime = bookProgressRepository.sumTotalReadingTimeByUserId(user.getId());

        return UserProfileResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .createdAt(user.getCreatedAt())
                .totalBooks(totalBooks)
                .totalSavedWords(totalSavedWords)
                .totalReadingTime(totalReadingTime != null ? totalReadingTime : 0L)
                .build();
    }

    public UserProfileResponse updateUserProfile(String username, UserProfileUpdateRequest request) {
        User user = findByUsername(username);

        // Kullanıcı adı değişikliği kontrolü
        if (!user.getUsername().equals(request.getUsername())) {
            if (userRepository.findByUsername(request.getUsername()).isPresent()) {
                throw new RuntimeException("Bu kullanıcı adı zaten kullanımda");
            }
            user.setUsername(request.getUsername());
        }

        // Email değişikliği kontrolü
        if (!user.getEmail().equals(request.getEmail())) {
            if (userRepository.findByEmail(request.getEmail()).isPresent()) {
                throw new RuntimeException("Bu email zaten kullanımda");
            }
            user.setEmail(request.getEmail());
        }

        // Şifre değişikliği kontrolü
        if (request.getCurrentPassword() != null && request.getNewPassword() != null) {
            if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
                throw new RuntimeException("Mevcut şifre yanlış");
            }
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        }

        userRepository.save(user);
        return getUserProfile(user.getUsername());
    }
}