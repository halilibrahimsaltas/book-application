package com.example.book_application.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.book_application.model.Pronunciation;

@Repository
public interface PronunciationRepository extends JpaRepository<Pronunciation, Long> {
    List<Pronunciation> findByWordId(Long wordId);
} 