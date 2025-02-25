package com.example.book_application.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.example.book_application.model.Pronunciation;
import com.example.book_application.service.PronunciationService;
import com.example.book_application.model.Word;
import com.example.book_application.service.WordService;
import com.example.book_application.dto.PronunciationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/pronunciations")
@RequiredArgsConstructor
@Slf4j
public class PronunciationController {

    private final PronunciationService pronunciationService;
    private final WordService wordService;

    @PostMapping
    public Pronunciation createPronunciation(@RequestBody PronunciationRequest request) {
        try {
            log.info("Creating pronunciation for word ID: {}", request.getWordId());
            Word word = wordService.findById(request.getWordId());
            
            Pronunciation pronunciation = new Pronunciation();
            pronunciation.setWord(word);
            pronunciation.setAudioUrl(request.getAudioUrl());
            pronunciation.setLanguage(request.getLanguage());
            
            return pronunciationService.savePronunciation(pronunciation);
        } catch (Exception e) {
            log.error("Error creating pronunciation: {}", e.getMessage());
            throw e;
        }
    }

    @GetMapping("/word/{wordId}")
    public List<Pronunciation> getPronunciationsByWordId(@PathVariable Long wordId) {
        log.info("Fetching pronunciations for word ID: {}", wordId);
        return pronunciationService.findByWordId(wordId);
    }

    @GetMapping
    public List<Pronunciation> getAllPronunciations() {
        log.info("Fetching all pronunciations");
        return pronunciationService.findAllPronunciations();
    }

    @GetMapping("/{id}")
    public Pronunciation getPronunciationById(@PathVariable Long id) {
        log.info("Fetching pronunciation with ID: {}", id);
        return pronunciationService.findById(id);
    }

    @PutMapping("/{id}")
    public Pronunciation updatePronunciation(@PathVariable Long id, @RequestBody Pronunciation pronunciation) {
        log.info("Updating pronunciation with ID: {}", id);
        return pronunciationService.updatePronunciation(id, pronunciation);
    }

    @DeleteMapping("/{id}")
    public void deletePronunciation(@PathVariable Long id) {
        log.info("Deleting pronunciation with ID: {}", id);
        pronunciationService.deletePronunciation(id);
    }
}