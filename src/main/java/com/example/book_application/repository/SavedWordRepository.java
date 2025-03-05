package com.example.book_application.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.book_application.model.SavedWord;

@Repository
public interface SavedWordRepository extends JpaRepository<SavedWord, Long> {
    List<SavedWord> findByUserId(Long userId);
    Optional<SavedWord> findByUserIdAndEnglish_Id(Long userId, Long englishId);
} 