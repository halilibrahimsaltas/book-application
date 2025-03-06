package com.example.book_application.repository;

import com.example.book_application.model.BookProgress;
import com.example.book_application.model.User;
import com.example.book_application.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookProgressRepository extends JpaRepository<BookProgress, Long> {
    Optional<BookProgress> findByUserAndBook_Id(User user, Long bookId);
    Optional<BookProgress> findByUserAndBook(User user, Book book);
} 