package com.example.book_application.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.book_application.core.excepiton.ResourceNotFoundException;
import com.example.book_application.model.Translation;
import com.example.book_application.service.TranslationService;

import lombok.RequiredArgsConstructor;
import java.util.List;

@RestController
@RequestMapping("/api/translations")
@RequiredArgsConstructor
public class TranslationController {

    private final TranslationService translationService;

    @GetMapping
    public List<Translation> getAllTranslations() {
        return translationService.findAllTranslations();
    }

    @PostMapping("/word/{wordId}")
    public Translation createTranslation(@PathVariable Long wordId, @RequestBody Translation translation) {
        return translationService.saveTranslation(wordId, translation)
                .orElseThrow(() -> new ResourceNotFoundException("Word not found with id: " + wordId));
    }
}