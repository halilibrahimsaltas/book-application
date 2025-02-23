package com.example.book_application.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.book_application.model.SavedWord;
import com.example.book_application.service.SavedWordService;    


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/saved-words")
@RequiredArgsConstructor
@Slf4j
public class SavedWordController {

    private final SavedWordService savedWordService;

    @PostMapping
    public SavedWord saveWord(@RequestBody SavedWord savedWord) {
        try {
            log.info("Saving word for user: {}", savedWord.getUser().getUsername());
            return savedWordService.saveSavedWord(savedWord);
        } catch (Exception e) {
            log.error("Error saving word: {}", e.getMessage());
            throw e;
        }
    }

    @GetMapping("/user/{userId}")
    public List<SavedWord> getSavedWordsByUser(@PathVariable Long userId) {
        log.info("Fetching saved words for user ID: {}", userId);
        return savedWordService.findByUserId(userId);
    }

    @GetMapping
    public List<SavedWord> getAllSavedWords() {
        try {
            log.info("Fetching all saved words");
            return savedWordService.findAllSavedWords();
        } catch (Exception e) {
            log.error("Error fetching saved words: {}", e.getMessage());
            throw e;
        }
    }
}