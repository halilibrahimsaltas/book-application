package com.example.book_application.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.http.ResponseEntity;

import com.example.book_application.model.SavedWord;
import com.example.book_application.service.SavedWordService;    
import com.example.book_application.model.User;
import com.example.book_application.service.UserService;
import com.example.book_application.dto.SavedWordRequest;
import com.example.book_application.dto.SavedWordResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/saved-words")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class SavedWordController {

    private final SavedWordService savedWordService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<SavedWord> saveWord(@RequestBody SavedWordRequest request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User user = userService.findByUsername(username);
            
            log.info("Saving word for user: {}", username);
            
            SavedWord savedWord = savedWordService.saveSavedWord(request, user);
            return ResponseEntity.ok(savedWord);
        } catch (Exception e) {
            log.error("Error saving word: {}", e.getMessage());
            throw e;
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<SavedWordResponse>> getSavedWordsByUser(@PathVariable Long userId) {
        log.info("Fetching saved words for user ID: {}", userId);
        return ResponseEntity.ok(savedWordService.findByUserIdAsDTO(userId));
    }

    @GetMapping
    public ResponseEntity<List<SavedWordResponse>> getAllSavedWords() {
        try {
            log.info("Fetching all saved words");
            return ResponseEntity.ok(savedWordService.findAllSavedWordsAsDTO());
        } catch (Exception e) {
            log.error("Error fetching saved words: {}", e.getMessage());
            throw e;
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<SavedWordResponse> getSavedWordById(@PathVariable Long id) {
        log.info("Fetching saved word with ID: {}", id);
        return ResponseEntity.ok(savedWordService.findByIdAsDTO(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SavedWord> updateSavedWord(
            @PathVariable Long id, 
            @RequestBody SavedWordRequest request) {
        log.info("Updating saved word with ID: {}", id);
        return ResponseEntity.ok(savedWordService.updateSavedWord(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSavedWord(@PathVariable Long id) {
        log.info("Deleting saved word with ID: {}", id);
        savedWordService.deleteSavedWord(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/user")
    public ResponseEntity<List<SavedWordResponse>> getCurrentUserSavedWords() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUsername(username);
        return ResponseEntity.ok(savedWordService.findByUserIdAsDTO(user.getId()));
    }
}