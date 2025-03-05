package com.example.book_application.service;

import java.util.List;
import java.util.stream.Collectors;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.book_application.model.*;
import com.example.book_application.repository.*;
import com.example.book_application.core.excepiton.ResourceNotFoundException;
import com.example.book_application.dto.SavedWordRequest;
import com.example.book_application.dto.SavedWordResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;       

@Service
@Slf4j
@RequiredArgsConstructor
public class SavedWordService {

    private final SavedWordRepository savedWordRepository;
  
    private final BookRepository bookRepository;
    private final ENRepository enRepository;
    private final TRRepository trRepository;
    private final TypeRepository typeRepository;
    private final CategoryRepository categoryRepository;

    @Transactional
    public SavedWord saveSavedWord(SavedWordRequest request, User user) {
        try {
            // Book kontrolü
            Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new ResourceNotFoundException("Book not found"));

            // EN kelime kontrolü   
            English english = enRepository.findByWord(request.getEnWord());
            if (english == null) {
                english = new English();
                english.setWord(request.getEnWord());
                english = enRepository.save(english);
            }

            // TR kelime kontrolü
            Turkish turkish = trRepository.findByWord(request.getTrWord());
            if (turkish == null) {
                turkish = new Turkish();
                turkish.setWord(request.getTrWord());
                turkish = trRepository.save(turkish);
            }

            // Type kontrolü (opsiyonel)
            Type type = null;
            if (request.getType() != null) {
                type = typeRepository.findByName(request.getType());
                if (type == null) {
                    type = new Type();
                    type.setName(request.getType());
                    type = typeRepository.save(type);
                }
            }

            // Category kontrolü (opsiyonel)
            Category category = null;
            if (request.getCategory() != null) {
                category = categoryRepository.findByName(request.getCategory());
                if (category == null) {
                    category = new Category();
                    category.setName(request.getCategory());
                    category = categoryRepository.save(category);
                }
            }

            // SavedWord oluştur
            SavedWord savedWord = new SavedWord();
            savedWord.setUser(user);
            savedWord.setBook(book);
            savedWord.setEnglish(english);
            savedWord.setTurkish(turkish);
            savedWord.setType(type);
            savedWord.setCategory(category);

            return savedWordRepository.save(savedWord);
        } catch (Exception e) {
            log.error("Error saving word: {}", e.getMessage());
            throw e;
        }
    }

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

    @Transactional
    public SavedWord updateSavedWord(Long id, SavedWordRequest request) {
        SavedWord savedWord = findById(id);
        
        if (request.getTrWord() != null) {
            Turkish tr = trRepository.findByWord(request.getTrWord());
            if (tr == null) {
                tr = new Turkish();
                tr.setWord(request.getTrWord());
                tr = trRepository.save(tr);
            }
            savedWord.setTurkish(tr);
        }

        if (request.getType() != null) {
            Type type = typeRepository.findByName(request.getType());
            if (type == null) {
                type = new Type();
                type.setName(request.getType());
                type = typeRepository.save(type);
            }
            savedWord.setType(type);
        }

        if (request.getCategory() != null) {
            Category category = categoryRepository.findByName(request.getCategory());
            if (category == null) {
                category = new Category();
                category.setName(request.getCategory());
                category = categoryRepository.save(category);
            }
            savedWord.setCategory(category);
        }

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
        wordDTO.setEnWord(savedWord.getEnglish().getWord());
        wordDTO.setTrWord(savedWord.getTurkish().getWord());
        if (savedWord.getType() != null) {
            wordDTO.setType(savedWord.getType().getName());
        }
        if (savedWord.getCategory() != null) {
            wordDTO.setCategory(savedWord.getCategory().getName());
        }
        dto.setWord(wordDTO);
        dto.setSavedDate(savedWord.getSavedDate());
        
        return dto;
    }
}