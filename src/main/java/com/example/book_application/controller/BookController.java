package com.example.book_application.controller;

import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/title/{title}")
    public Book getBookByTitle(@PathVariable String title) {
        log.info("Fetching book by title: {}", title);
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

    @GetMapping("/{id}")
    public Book getBookById(@PathVariable Long id) {
        log.info("Fetching book with ID: {}", id);
        return bookService.findById(id);
    }

    @PutMapping("/{id}")
    public Book updateBook(@PathVariable Long id, @RequestBody Book book) {
        log.info("Updating book with ID: {}", id);
        return bookService.updateBook(id, book);
    }

    @DeleteMapping("/{id}")
    public void deleteBook(@PathVariable Long id) {
        log.info("Deleting book with ID: {}", id);
        bookService.deleteBook(id);
    }
}