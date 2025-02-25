package com.example.book_application.service;


import org.springframework.stereotype.Service;

import com.example.book_application.model.Book;
import com.example.book_application.repository.BookRepository;
import com.example.book_application.core.excepiton.ResourceNotFoundException;

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

    public Book findById(Long id) {
        return bookRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));
    }

    public Book updateBook(Long id, Book bookDetails) {
        Book book = findById(id);
        book.setTitle(bookDetails.getTitle());
        book.setAuthor(bookDetails.getAuthor());
        return bookRepository.save(book);
    }

    public void deleteBook(Long id) {
        Book book = findById(id);
        bookRepository.delete(book);
    }
}