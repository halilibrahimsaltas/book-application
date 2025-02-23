package com.example.book_application.service;


import org.springframework.stereotype.Service;

import com.example.book_application.model.Book;
import com.example.book_application.repository.BookRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;

    public Book saveBook(Book book) {
        Book savedBook = bookRepository.save(book);
        return savedBook;
    }


    public Book findByTitle(String title) {
        log.info("Finding book by title: {}", title);
        Book book = bookRepository.findByTitle(title);
        if (book == null) {
            log.debug("No book found with title: {}", title);
        }
        return book;
    }

    public List<Book> findAllBooks() {
        return bookRepository.findAll();
    }
}