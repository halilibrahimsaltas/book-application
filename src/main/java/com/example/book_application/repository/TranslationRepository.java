package com.example.book_application.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.book_application.model.Translation;

@Repository
public interface TranslationRepository extends JpaRepository<Translation, Long> {
    List<Translation> findByWordId(Long wordId);
}