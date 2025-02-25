package com.example.book_application.controller;

import org.springframework.web.bind.annotation.*;

import com.example.book_application.core.excepiton.ResourceNotFoundException;
import com.example.book_application.model.Translation;
import com.example.book_application.service.TranslationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.List;

@RestController
@RequestMapping("/api/translations")
@RequiredArgsConstructor
@Slf4j
public class TranslationController {

    private final TranslationService translationService;

    @GetMapping
    public List<Translation> getAllTranslations() {
        log.info("Fetching all translations");
        return translationService.findAllTranslations();
    }

    @PostMapping("/word/{wordId}")
    public Translation createTranslation(@PathVariable Long wordId, @RequestBody Translation translation) {
        log.info("Creating translation for word ID: {}", wordId);
        return translationService.saveTranslation(wordId, translation)
                .orElseThrow(() -> new ResourceNotFoundException("Word not found with id: " + wordId));
    }

    @GetMapping("/word/{wordId}")
    public List<Translation> getTranslationsByWordId(@PathVariable Long wordId) {
        log.info("Fetching translations for word ID: {}", wordId);
        return translationService.findByWordId(wordId);
    }

    @GetMapping("/{id}")
    public Translation getTranslationById(@PathVariable Long id) {
        log.info("Fetching translation with ID: {}", id);
        return translationService.findById(id);
    }

    @PutMapping("/{id}")
    public Translation updateTranslation(@PathVariable Long id, @RequestBody Translation translation) {
        log.info("Updating translation with ID: {}", id);
        return translationService.updateTranslation(id, translation);
    }

    @DeleteMapping("/{id}")
    public void deleteTranslation(@PathVariable Long id) {
        log.info("Deleting translation with ID: {}", id);
        translationService.deleteTranslation(id);
    }
}