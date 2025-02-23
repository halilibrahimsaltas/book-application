package com.example.book_application.service;

import java.util.List;
import java.util.Optional;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.book_application.model.Translation;
import com.example.book_application.repository.TranslationRepository;
import com.example.book_application.repository.WordRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class TranslationService {

    private final TranslationRepository translationRepository;
    private final WordRepository wordRepository;

    // Save a translation
    @Transactional
    @CacheEvict(value = "translations", key = "#wordId")
    public Optional<Translation> saveTranslation(Long wordId, Translation translation) {
        log.info("Saving translation for word ID: {}", wordId);
        return wordRepository.findById(wordId).map(word -> {
            translation.setWord(word);
            log.debug("Translation saved successfully");
            return translationRepository.save(translation);
        });
    }

    // Find translations by word ID
    @Cacheable(value = "translations", key = "#wordId")
    public List<Translation> findByWordId(Long wordId) {
        log.info("Finding translations for word ID: {}", wordId);
        List<Translation> translations = translationRepository.findByWordId(wordId);
        log.debug("Found {} translations", translations.size());
        return translations;
    }

    public List<Translation> findAllTranslations() {
        return translationRepository.findAll();
    }
}