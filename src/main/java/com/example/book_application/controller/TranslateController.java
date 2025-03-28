package com.example.book_application.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import lombok.RequiredArgsConstructor;
import com.example.book_application.service.TranslateService;
import com.example.book_application.dto.*;
import java.util.List;
import com.example.book_application.repository.TranslateRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/translates")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class TranslateController {

    private final TranslateService translateService;
    private final TranslateRepository translateRepository;

    @PostMapping
    public ResponseEntity<TranslateResponse> createTranslate(@RequestBody TranslateRequest request) {
        try {
            TranslateResponse response = translateService.createTranslate(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Çeviri oluşturma hatası: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping
    public ResponseEntity<List<TranslateResponse>> getAllTranslates() {
        try {
            List<TranslateResponse> translations = translateService.getAllTranslates();
            return ResponseEntity.ok(translations);
        } catch (Exception e) {
            log.error("Tüm çevirileri getirme hatası: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/en/{word}")
    public ResponseEntity<List<TranslateResponse>> getTranslationsByEnglishWord(
            @PathVariable String word) {
        try {
            log.debug("İngilizce kelime için çeviri aranıyor: {}", word);
            List<TranslateResponse> translations = translateRepository
                .findTranslationsByWord(word.toLowerCase().trim());
            
            if (translations.isEmpty()) {
                log.debug("Kelime için çeviri bulunamadı: {}", word);
                return ResponseEntity.ok(translations);
            }
            
            return ResponseEntity.ok(translations);
        } catch (Exception e) {
            log.error("Çeviri arama hatası '{}': {}", word, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/tr/{word}")
    public ResponseEntity<List<TranslateResponse>> getTranslationsByTurkishWord(
            @PathVariable String word) {
        try {
            log.debug("Türkçe kelime için çeviri aranıyor: {}", word);
            List<TranslateResponse> translations = translateRepository
                .findTranslationsByTurkishWord(word.toLowerCase().trim());
            
            if (translations.isEmpty()) {
                log.debug("Kelime için çeviri bulunamadı: {}", word);
                return ResponseEntity.ok(translations);
            }
            
            return ResponseEntity.ok(translations);
        } catch (Exception e) {
            log.error("Çeviri arama hatası '{}': {}", word, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/libre")
    public ResponseEntity<?> translateWithLibre(@RequestParam String word) {
        try {
            log.debug("LibreTranslate ile çeviri yapılıyor ve kaydediliyor: {}", word);
            TranslateResponse response = translateService.translateAndSave(word);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("LibreTranslate çeviri hatası: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Çeviri sırasında bir hata oluştu: " + e.getMessage());
        }
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/libre/raw")
    public ResponseEntity<?> translateOnly(
            @RequestParam String text, 
            @RequestParam(defaultValue = "en") String from,
            @RequestParam(defaultValue = "tr") String to) {
        try {
            log.debug("LibreTranslate ile ham çeviri yapılıyor: {} ({} -> {})", text, from, to);
            String translation = translateService.translateWithLibre(text, from, to);
            return ResponseEntity.ok(translation);
        } catch (Exception e) {
            log.error("LibreTranslate ham çeviri hatası: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Çeviri sırasında bir hata oluştu: " + e.getMessage());
        }
    }
} 