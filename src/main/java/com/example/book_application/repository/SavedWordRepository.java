package com.example.book_application.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.book_application.model.SavedWord;

@Repository
public interface SavedWordRepository extends JpaRepository<SavedWord, Long> {
    List<SavedWord> findByUserId(Long userId);
    List<SavedWord> findByBookIdAndUserId(Long bookId, Long userId);
    boolean existsByUserIdAndEnglishWord(Long userId, String englishWord);
    Optional<SavedWord> findByUserIdAndEnglishId(Long userId, Long englishId);
} 