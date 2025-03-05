package com.example.book_application.controller;

import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import com.example.book_application.service.TranslateService;
import com.example.book_application.dto.*;
import java.util.List;
import com.example.book_application.repository.TranslateRepository;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/translates")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TranslateController {

    private final TranslateService translateService;
    private final TranslateRepository translateRepository;

    @PostMapping
    public TranslateResponse createTranslate(@RequestBody TranslateRequest request) {
        return translateService.createTranslate(request);
    }

    @GetMapping
    public List<TranslateResponse> getAllTranslates() {
        return translateService.getAllTranslates();
    }

    @GetMapping("/en/{word}")
    public ResponseEntity<List<TranslateResponse>> getTranslations(@PathVariable String word) {
        List<TranslateResponse> translations = translateRepository.findTranslationsByWord(word);
        return ResponseEntity.ok(translations);
    }

    @GetMapping("/tr/{word}")
    public List<TranslateResponse> getTranslatesByTurkishWord(@PathVariable String word) {
        return translateService.getTranslatesByTurkishWord(word);
    }
} 