package com.example.book_application.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.book_application.model.Pronunciation;
import com.example.book_application.service.PronunciationService;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/pronunciations")
@RequiredArgsConstructor
@Slf4j
public class PronunciationController {

    @Autowired
    private PronunciationService pronunciationService;

    // Save a pronunciation
    @PostMapping
    public Pronunciation createPronunciation(@RequestBody Pronunciation pronunciation) {
        log.info("Creating pronunciation for word: {}", pronunciation.getWord().getWordText());
        return pronunciationService.savePronunciation(pronunciation);
    }

    // Get pronunciations by word ID
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

}