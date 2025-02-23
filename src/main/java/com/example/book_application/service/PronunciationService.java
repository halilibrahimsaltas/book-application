package com.example.book_application.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.book_application.model.Pronunciation;
import com.example.book_application.repository.PronunciationRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PronunciationService {
    private final PronunciationRepository pronunciationRepository;

    // Save a pronunciation
    public Pronunciation savePronunciation(Pronunciation pronunciation) {
        log.info("Saving pronunciation for word ID: {}", pronunciation.getWord().getId());
        return pronunciationRepository.save(pronunciation);
    }

    // Find pronunciations by word ID
    public List<Pronunciation> findByWordId(Long wordId) {
        log.info("Finding pronunciations for word ID: {}", wordId);
        return pronunciationRepository.findByWordId(wordId);
    }

    public List<Pronunciation> findAllPronunciations() {
        return pronunciationRepository.findAll();
    }
}