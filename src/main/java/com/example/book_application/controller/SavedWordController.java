package com.example.book_application.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.example.book_application.model.SavedWord;
import com.example.book_application.service.SavedWordService;    
import com.example.book_application.model.User;
import com.example.book_application.model.Book;
import com.example.book_application.model.Word;
import com.example.book_application.service.UserService;
import com.example.book_application.service.BookService;
import com.example.book_application.service.WordService;
import com.example.book_application.dto.SavedWordRequest;
import com.example.book_application.dto.SavedWordResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/saved-words")
@RequiredArgsConstructor
@Slf4j
public class SavedWordController {

    private final SavedWordService savedWordService;
    private final UserService userService;
    private final BookService bookService;
    private final WordService wordService;

    @PostMapping
    public SavedWord saveWord(@RequestBody SavedWordRequest request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User user = userService.findByUsername(username);
            
            log.info("Saving word for user: {}", username);
            
            Book book = bookService.findById(request.getBookId());
            Word word = wordService.findById(request.getWordId());
            
            SavedWord savedWord = new SavedWord();
            savedWord.setUser(user);
            savedWord.setBook(book);
            savedWord.setWord(word);
            
            return savedWordService.saveSavedWord(savedWord);
        } catch (Exception e) {
            log.error("Error saving word: {}", e.getMessage());
            throw e;
        }
    }

    @GetMapping("/user/{userId}")
    public List<SavedWordResponse> getSavedWordsByUser(@PathVariable Long userId) {
        log.info("Fetching saved words for user ID: {}", userId);
        return savedWordService.findByUserIdAsDTO(userId);
    }

    @GetMapping
    public List<SavedWordResponse> getAllSavedWords() {
        try {
            log.info("Fetching all saved words");
            return savedWordService.findAllSavedWordsAsDTO();
        } catch (Exception e) {
            log.error("Error fetching saved words: {}", e.getMessage());
            throw e;
        }
    }

    @GetMapping("/{id}")
    public SavedWordResponse getSavedWordById(@PathVariable Long id) {
        log.info("Fetching saved word with ID: {}", id);
        return savedWordService.findByIdAsDTO(id);
    }

    @PutMapping("/{id}")
    public SavedWord updateSavedWord(@PathVariable Long id, @RequestBody SavedWord savedWord) {
        log.info("Updating saved word with ID: {}", id);
        return savedWordService.updateSavedWord(id, savedWord);
    }

    @DeleteMapping("/{id}")
    public void deleteSavedWord(@PathVariable Long id) {
        log.info("Deleting saved word with ID: {}", id);
        savedWordService.deleteSavedWord(id);
    }

    @GetMapping("/user")
    public List<SavedWordResponse> getCurrentUserSavedWords() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUsername(username);
        return savedWordService.findByUserIdAsDTO(user.getId());
    }
}