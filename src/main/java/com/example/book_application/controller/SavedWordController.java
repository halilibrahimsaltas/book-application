package com.example.book_application.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.http.ResponseEntity;


import com.example.book_application.service.SavedWordService;    
import com.example.book_application.model.User;
import com.example.book_application.service.UserService;
import com.example.book_application.dto.SavedWordRequest;
import com.example.book_application.dto.SavedWordResponse;
import com.example.book_application.core.excepiton.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/saved-words")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@Slf4j
public class SavedWordController {

    private final SavedWordService savedWordService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<?> saveWord(@RequestBody SavedWordRequest request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Kullanıcı girişi yapılmamış");
            }

            String username = authentication.getName();
            User user = userService.findByUsername(username);
            
            log.info("Kelime kaydediliyor, kullanıcı: {}", username);
            
            // Kelime zaten kaydedilmiş mi kontrol et
            if (savedWordService.isWordAlreadySaved(user.getId(), request.getEnglishWord())) {
                return ResponseEntity.badRequest()
                    .body("Bu kelime zaten kaydedilmiş");
            }
            
            SavedWordResponse response = savedWordService.saveSavedWord(request, username);
            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException e) {
            log.error("Kaynak bulunamadı: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(e.getMessage());
        } catch (Exception e) {
            log.error("Kelime kaydedilirken hata oluştu: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Kelime kaydedilirken bir hata oluştu: " + e.getMessage());
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<SavedWordResponse>> getSavedWordsByUser(@PathVariable Long userId) {
        try {
            log.info("Kullanıcının kayıtlı kelimeleri getiriliyor, ID: {}", userId);
            return ResponseEntity.ok(savedWordService.findByUserIdAsDTO(userId));
        } catch (Exception e) {
            log.error("Kayıtlı kelimeler getirilirken hata oluştu: {}", e.getMessage());
            throw e;
        }
    }

    @GetMapping
    public ResponseEntity<List<SavedWordResponse>> getAllSavedWords() {
        try {
            log.info("Tüm kayıtlı kelimeler getiriliyor");
            return ResponseEntity.ok(savedWordService.findAllSavedWordsAsDTO());
        } catch (Exception e) {
            log.error("Tüm kelimeler getirilirken hata oluştu: {}", e.getMessage());
            throw e;
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getSavedWordById(@PathVariable Long id) {
        try {
            log.info("Kayıtlı kelime getiriliyor, ID: {}", id);
            return ResponseEntity.ok(savedWordService.findByIdAsDTO(id));
        } catch (ResourceNotFoundException e) {
            log.error("Kelime bulunamadı: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(e.getMessage());
        } catch (Exception e) {
            log.error("Kelime getirilirken hata oluştu: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Kelime getirilirken bir hata oluştu: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateSavedWord(@PathVariable Long id, @RequestBody SavedWordRequest request) {
        try {
            log.info("Kayıtlı kelime güncelleniyor, ID: {}", id);
            return ResponseEntity.ok(savedWordService.updateSavedWord(id, request));
        } catch (ResourceNotFoundException e) {
            log.error("Güncellenecek kelime bulunamadı: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(e.getMessage());
        } catch (Exception e) {
            log.error("Kelime güncellenirken hata oluştu: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Kelime güncellenirken bir hata oluştu: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSavedWord(@PathVariable Long id) {
        try {
            log.info("Kayıtlı kelime siliniyor, ID: {}", id);
            savedWordService.deleteSavedWord(id);
            return ResponseEntity.ok().body("Kelime başarıyla silindi");
        } catch (ResourceNotFoundException e) {
            log.error("Silinecek kelime bulunamadı: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(e.getMessage());
        } catch (Exception e) {
            log.error("Kelime silinirken hata oluştu: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Kelime silinirken bir hata oluştu: " + e.getMessage());
        }
    }

    @GetMapping("/user")
    public ResponseEntity<?> getCurrentUserSavedWords() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Kullanıcı girişi yapılmamış");
            }

            String username = authentication.getName();
            User user = userService.findByUsername(username);
            log.info("Mevcut kullanıcının kayıtlı kelimeleri getiriliyor: {}", username);
            return ResponseEntity.ok(savedWordService.findByUserIdAsDTO(user.getId()));
        } catch (Exception e) {
            log.error("Kullanıcının kelimeleri getirilirken hata oluştu: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Kelimeler getirilirken bir hata oluştu: " + e.getMessage());
        }
    }

    @GetMapping("/book/{bookId}")
    public ResponseEntity<?> getSavedWordsByBook(@PathVariable Long bookId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Kullanıcı girişi yapılmamış");
            }

            String username = authentication.getName();
            User user = userService.findByUsername(username);
            
            log.info("Kitaba ait kayıtlı kelimeler getiriliyor, Kitap ID: {}, Kullanıcı: {}", bookId, username);
            List<SavedWordResponse> words = savedWordService.findByBookIdAndUserIdAsDTO(bookId, user.getId());
            return ResponseEntity.ok(words);
        } catch (ResourceNotFoundException e) {
            log.error("Kitap veya kelimeler bulunamadı: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(e.getMessage());
        } catch (Exception e) {
            log.error("Kitaba ait kelimeler getirilirken hata oluştu: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Kelimeler getirilirken bir hata oluştu: " + e.getMessage());
        }
    }
}