package com.example.book_application.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.book_application.model.Pronunciation;
import com.example.book_application.repository.PronunciationRepository;
import com.example.book_application.repository.WordRepository;
import com.example.book_application.core.excepiton.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PronunciationService {
    private final PronunciationRepository pronunciationRepository;
    private final WordRepository wordRepository;

    // Save a pronunciation
    public Pronunciation savePronunciation(Pronunciation pronunciation) {
        try {
            log.info("Saving pronunciation for word ID: {}", pronunciation.getWord().getId());
            return pronunciationRepository.save(pronunciation);
        } catch (Exception e) {
            log.error("Error saving pronunciation: {}", e.getMessage());
            throw e;
        }
    }

    // Find pronunciations by word ID
    public List<Pronunciation> findByWordId(Long wordId) {
        log.info("Finding pronunciations for word ID: {}", wordId);
        wordRepository.findById(wordId)
            .orElseThrow(() -> new ResourceNotFoundException("Word not found with id: " + wordId));
        return pronunciationRepository.findByWordId(wordId);
    }

    public List<Pronunciation> findAllPronunciations() {
        try {
            return pronunciationRepository.findAll();
        } catch (Exception e) {
            log.error("Error fetching all pronunciations: {}", e.getMessage());
            throw e;
        }
    }

    public Pronunciation findById(Long id) {
        return pronunciationRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Pronunciation not found with id: " + id));
    }

    public Pronunciation updatePronunciation(Long id, Pronunciation pronunciationDetails) {
        Pronunciation pronunciation = findById(id);
        pronunciation.setAudioUrl(pronunciationDetails.getAudioUrl());
        return pronunciationRepository.save(pronunciation);
    }

    public void deletePronunciation(Long id) {
        Pronunciation pronunciation = findById(id);
        pronunciationRepository.delete(pronunciation);
    }
}