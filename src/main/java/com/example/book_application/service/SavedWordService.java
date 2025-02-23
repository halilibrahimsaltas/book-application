package com.example.book_application.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.book_application.model.SavedWord; 
import com.example.book_application.repository.SavedWordRepository;
import com.example.book_application.repository.UserRepository;
import com.example.book_application.repository.WordRepository;
import com.example.book_application.repository.BookRepository;
import com.example.book_application.core.excepiton.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class SavedWordService {

    private final SavedWordRepository savedWordRepository;
    private final UserRepository userRepository;
    private final WordRepository wordRepository;
    private final BookRepository bookRepository;

    // Save a saved word
    public SavedWord saveSavedWord(SavedWord savedWord) {
        try {
            // İlişkili entity'lerin varlığını kontrol et
            userRepository.findById(savedWord.getUser().getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
            
            wordRepository.findById(savedWord.getWord().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Word not found"));
            
            bookRepository.findById(savedWord.getBook().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Book not found"));

            return savedWordRepository.save(savedWord);
        } catch (Exception e) {
            log.error("Error saving word: {}", e.getMessage());
            throw e;
        }
    }

    // Find saved words by user ID
    public List<SavedWord> findByUserId(Long userId) {
        log.info("Finding saved words for user ID: {}", userId);
        List<SavedWord> savedWords = savedWordRepository.findByUserId(userId);
        log.debug("Found {} saved words", savedWords.size());
        return savedWords;
    }

    public List<SavedWord> findAllSavedWords() {
        try {
            return savedWordRepository.findAll();
        } catch (Exception e) {
            log.error("Error fetching saved words: {}", e.getMessage());
            throw e;
        }
    }
}