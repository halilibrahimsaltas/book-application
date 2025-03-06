package com.example.book_application.service;

import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

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
@Transactional
@Slf4j
@RequiredArgsConstructor
public class SavedWordService {

    private final SavedWordRepository savedWordRepository;
    private final UserService userService;
    private final BookService bookService;
    private final ENRepository enRepository;
    private final TRRepository trRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    public boolean isWordAlreadySaved(Long userId, String englishWord) {
        English english = enRepository.findByWord(englishWord);
        if (english == null) {
            return false;
        }
        return savedWordRepository.findByUserIdAndEnglishId(userId, english.getId()).isPresent();
    }

    public SavedWordResponse saveSavedWord(SavedWordRequest request, String username) {
        log.info("Kelime kaydetme işlemi başlatıldı. Kullanıcı: {}, Kelime: {}", username, request.getEnglishWord());
        
        try {
            User user = userService.findByUsername(username);
            if (user == null) {
                throw new ResourceNotFoundException("Kullanıcı bulunamadı: " + username);
            }

            Book book = null;
            if (request.getBookId() != null) {
                book = bookService.findById(request.getBookId());
                if (book == null) {
                    throw new ResourceNotFoundException("Kitap bulunamadı: " + request.getBookId());
                }
            }

            English english = enRepository.findByWord(request.getEnglishWord());
            if (english == null) {
                english = new English();
                english.setWord(request.getEnglishWord());
                english = enRepository.save(english);
            }

            Turkish turkish = trRepository.findByWord(request.getTurkishWord());
            if (turkish == null) {
                turkish = new Turkish();
                turkish.setWord(request.getTurkishWord());
                turkish = trRepository.save(turkish);
            }

            boolean isWordAlreadySaved = isWordAlreadySaved(user.getId(), request.getEnglishWord());
            if (isWordAlreadySaved) {
                throw new IllegalStateException("Bu kelime zaten kaydedilmiş: " + request.getEnglishWord());
            }

            SavedWord savedWord = new SavedWord();
            savedWord.setUser(user);
            savedWord.setBook(book);
            savedWord.setEnglish(english);
            savedWord.setTurkish(turkish);
            savedWord.setSavedDate(LocalDateTime.now());

            savedWord = savedWordRepository.save(savedWord);
            log.info("Kelime başarıyla kaydedildi. ID: {}", savedWord.getId());

            return SavedWordResponse.builder()
                    .id(savedWord.getId())
                    .englishWord(english.getWord())
                    .turkishWord(turkish.getWord())
                    .bookId(book != null ? book.getId() : null)
                    .bookTitle(book != null ? book.getTitle() : null)
                    .createdAt(savedWord.getSavedDate())
                    .build();

        } catch (Exception e) {
            log.error("Kelime kaydedilirken hata oluştu: {}", e.getMessage());
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
        
        if (request.getTurkishWord() != null) {
            Turkish tr = trRepository.findByWord(request.getTurkishWord());
            if (tr == null) {
                tr = new Turkish();
                tr.setWord(request.getTurkishWord());
                tr = trRepository.save(tr);
            }
            savedWord.setTurkish(tr);
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

    @Transactional(readOnly = true)
    public List<SavedWordResponse> findByBookIdAndUserIdAsDTO(Long bookId, Long userId) {
        Book book = bookRepository.findById(bookId)
            .orElseThrow(() -> new ResourceNotFoundException("Kitap bulunamadı"));
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Kullanıcı bulunamadı"));

        List<SavedWord> savedWords = savedWordRepository.findByBookIdAndUserId(bookId, userId);
        
        return savedWords.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    private SavedWordResponse convertToDTO(SavedWord savedWord) {
        return SavedWordResponse.builder()
            .id(savedWord.getId())
            .englishWord(savedWord.getEnglish().getWord())
            .turkishWord(savedWord.getTurkish().getWord())
            .bookId(savedWord.getBook() != null ? savedWord.getBook().getId() : null)
            .bookTitle(savedWord.getBook() != null ? savedWord.getBook().getTitle() : null)
            .createdAt(savedWord.getSavedDate())
            .build();
    }
}