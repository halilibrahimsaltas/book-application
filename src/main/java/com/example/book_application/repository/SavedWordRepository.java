package com.example.book_application.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.book_application.model.SavedWord;
import com.example.book_application.model.User;
import com.example.book_application.model.Book;


@Repository
public interface SavedWordRepository extends JpaRepository<SavedWord, Long> {
    List<SavedWord> findByUserAndBook(User user, Book book);
    Optional<SavedWord> findByUserAndBookAndEnglish_Word(User user, Book book, String word);
    Page<SavedWord> findByUser(User user, Pageable pageable);
    List<SavedWord> findByUser(User user);

    @Query("SELECT COUNT(sw) FROM SavedWord sw WHERE sw.user.id = ?1")
    Integer countByUserId(Long userId);

    @Query("SELECT sw FROM SavedWord sw WHERE sw.user.id = ?1 AND sw.english.id = ?2")
    Optional<SavedWord> findByUserIdAndEnglishId(Long userId, Long englishId);

    @Query("SELECT sw FROM SavedWord sw WHERE sw.user.id = ?1")
    List<SavedWord> findByUserId(Long userId);

    @Query("SELECT sw FROM SavedWord sw WHERE sw.book.id = ?1 AND sw.user.id = ?2")
    List<SavedWord> findByBookIdAndUserId(Long bookId, Long userId);
} 