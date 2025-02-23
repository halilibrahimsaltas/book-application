package com.example.book_application.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.book_application.core.excepiton.ResourceNotFoundException;
import com.example.book_application.model.Book;
import com.example.book_application.service.BookService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@Slf4j
public class BookController {

    private final BookService bookService;

    @PostMapping
    public Book createBook(@RequestBody Book book) {
        log.info("Creating book: {}", book.getTitle());
        return bookService.saveBook(book);
    }

    @GetMapping("/{title}")
    public Book getBookByTitle(@PathVariable String title) {
        Book book = bookService.findByTitle(title);
        if (book == null) {
            throw new ResourceNotFoundException("Book not found with title: " + title);
        }
        return book;
    }

    @GetMapping
    public List<Book> getAllBooks() {
        log.info("Fetching all books");
        return bookService.findAllBooks();
    }
}