package com.example.book_application.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.book_application.model.SavedWord; 
import com.example.book_application.repository.SavedWordRepository;
import com.example.book_application.repository.UserRepository;
import com.example.book_application.repository.WordRepository;
import com.example.book_application.repository.BookRepository;
import com.example.book_application.core.excepiton.ResourceNotFoundException;
import com.example.book_application.dto.SavedWordResponse;

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

    public SavedWord findById(Long id) {
        return savedWordRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Saved word not found with id: " + id));
    }

    public SavedWord updateSavedWord(Long id, SavedWord savedWordDetails) {
        SavedWord savedWord = findById(id);
        savedWord.setWord(savedWordDetails.getWord());
        savedWord.setBook(savedWordDetails.getBook());
        return savedWordRepository.save(savedWord);
    }

    public void deleteSavedWord(Long id) {
        SavedWord savedWord = findById(id);
        savedWordRepository.delete(savedWord);
    }

    public SavedWordResponse findByIdAsDTO(Long id) {
        SavedWord savedWord = findById(id);
        return convertToDTO(savedWord);
    }

    public List<SavedWordResponse> findAllSavedWordsAsDTO() {
        return findAllSavedWords().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    public List<SavedWordResponse> findByUserIdAsDTO(Long userId) {
        return findByUserId(userId).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    private SavedWordResponse convertToDTO(SavedWord savedWord) {
        SavedWordResponse dto = new SavedWordResponse();
        dto.setId(savedWord.getId());
        
        SavedWordResponse.UserDTO userDTO = new SavedWordResponse.UserDTO();
        userDTO.setId(savedWord.getUser().getId());
        userDTO.setUsername(savedWord.getUser().getUsername());
        userDTO.setEmail(savedWord.getUser().getEmail());
        dto.setUser(userDTO);
        
        SavedWordResponse.BookDTO bookDTO = new SavedWordResponse.BookDTO();
        bookDTO.setId(savedWord.getBook().getId());
        bookDTO.setTitle(savedWord.getBook().getTitle());
        bookDTO.setAuthor(savedWord.getBook().getAuthor());
        dto.setBook(bookDTO);
        
        SavedWordResponse.WordDTO wordDTO = new SavedWordResponse.WordDTO();
        wordDTO.setId(savedWord.getWord().getId());
        wordDTO.setWord(savedWord.getWord().getWord());
        wordDTO.setType(savedWord.getWord().getType());
        wordDTO.setCategory(savedWord.getWord().getCategory());
        wordDTO.setTr(savedWord.getWord().getTr());
        dto.setWord(wordDTO);
        
        return dto;
    }

}