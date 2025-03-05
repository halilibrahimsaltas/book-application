package com.example.book_application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import com.example.book_application.model.*;
import com.example.book_application.repository.*;
import com.example.book_application.dto.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TranslateService {

    private final TranslateRepository translateRepository;
    private final ENRepository enRepository;
    private final TRRepository trRepository;
    private final TypeRepository typeRepository;
    private final CategoryRepository categoryRepository;

    @Transactional
    public TranslateResponse createTranslate(TranslateRequest request) {
        // İngilizce kelimeyi bul veya oluştur
        English en = enRepository.findByWord(request.getEnWord());  
        if (en == null) {
            en = new English();
            en.setWord(request.getEnWord());
            en = enRepository.save(en);
        }

        // Türkçe kelimeyi bul veya oluştur
        Turkish tr = trRepository.findByWord(request.getTrWord());
        if (tr == null) {
            tr = new Turkish();
            tr.setWord(request.getTrWord());
            tr = trRepository.save(tr);
        }

        // Kelime türünü bul veya oluştur
        Type type = null;
        if (request.getType() != null) {
            type = typeRepository.findByName(request.getType());
            if (type == null) {
                type = new Type();
                type.setName(request.getType());
                type = typeRepository.save(type);
            }
        }

        // Kategoriyi bul veya oluştur
        Category category = null;
        if (request.getCategory() != null) {
            category = categoryRepository.findByName(request.getCategory());
            if (category == null) {
                category = new Category();
                category.setName(request.getCategory());
                category = categoryRepository.save(category);
            }
        }

        // Çeviri zaten var mı kontrol et
        if (translateRepository.existsByEnglishAndTurkish(en, tr)) {
            throw new RuntimeException("Bu çeviri zaten mevcut!");
        }

        // Yeni çeviriyi oluştur
        Translate translate = new Translate();
        translate.setEnglish(en);
        translate.setTurkish(tr);
        translate.setType(type);
        translate.setCategory(category);

        translate = translateRepository.save(translate);
        return convertToResponse(translate);
    }

    public List<TranslateResponse> getAllTranslates() {
        return translateRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<TranslateResponse> getTranslatesByEnglishWord(String word) {
        if (word == null || word.trim().isEmpty()) {
            return List.of();
        }
        
        // Önce tam eşleşme ara
        English exactMatch = enRepository.findByWord(word);
        if (exactMatch != null) {
            return translateRepository.findByEnglish(exactMatch).stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
        }
        
        // Tam eşleşme yoksa, içinde geçenleri ara
        List<English> similarWords = enRepository.findByWordContainingIgnoreCase(word);
        return similarWords.stream()
                .flatMap(en -> translateRepository.findByEnglish(en).stream())
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<TranslateResponse> getTranslatesByTurkishWord(String word) {
        if (word == null || word.trim().isEmpty()) {
            return List.of();
        }
        
        // Kelimeyi küçük harfe çevir
        word = word.toLowerCase().trim();
        
        Turkish tr = trRepository.findByWord(word);
        if (tr == null) {
            return List.of();
        }
        return translateRepository.findByTurkish(tr).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    private TranslateResponse convertToResponse(Translate translate) {
        return new TranslateResponse(
            translate.getId(),
            translate.getEnglish().getWord(),
            translate.getTurkish().getWord(),
            translate.getType() != null ? translate.getType().getName() : null,
            translate.getCategory() != null ? translate.getCategory().getName() : null
        );
    }
} 