package com.example.book_application.repository;

import com.example.book_application.model.BookProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface BookProgressRepository extends JpaRepository<BookProgress, Long> {
    Optional<BookProgress> findByUserIdAndBookId(Long userId, Long bookId);
} 