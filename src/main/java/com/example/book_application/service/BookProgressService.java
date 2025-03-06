package com.example.book_application.service;

import com.example.book_application.model.Book;
import com.example.book_application.model.BookProgress;
import com.example.book_application.model.User;
import com.example.book_application.repository.BookProgressRepository;
import com.example.book_application.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookProgressService {

    private final BookProgressRepository bookProgressRepository;
    private final BookRepository bookRepository;
    private final UserService userService;

    @Transactional(readOnly = true)
    public BookProgress getProgress(Long bookId) {
        User currentUser = userService.getCurrentUser();
        return bookProgressRepository.findByUserAndBook_Id(currentUser, bookId)
                .orElse(new BookProgress());
    }

    @Transactional
    public BookProgress saveProgress(Long bookId, BookProgress progress) {
        User currentUser = userService.getCurrentUser();
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Kitap bulunamadı"));

        BookProgress existingProgress = bookProgressRepository
                .findByUserAndBook_Id(currentUser, bookId)
                .orElse(new BookProgress());

        existingProgress.setUser(currentUser);
        existingProgress.setBook(book);
        existingProgress.setCurrentPage(progress.getCurrentPage());
        existingProgress.setTotalPages(progress.getTotalPages());

        return bookProgressRepository.save(existingProgress);
    }
} 