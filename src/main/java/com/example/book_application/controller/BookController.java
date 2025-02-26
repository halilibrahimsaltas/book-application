package com.example.book_application.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.book_application.core.excepiton.ResourceNotFoundException;
import com.example.book_application.model.Book;
import com.example.book_application.service.BookService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import java.io.IOException;
import java.io.File;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@Slf4j
public class BookController {

    private final BookService bookService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Book createBook(
            @RequestPart("book") Book book,
            @RequestPart("file") MultipartFile file) throws IOException {
        
        log.info("Creating book: {}", book.getTitle());
        
        // Dosya boyutu kontrolü
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be empty");
        }
        
        // Geçici dosya oluştur
        File tempFile = File.createTempFile("upload_", ".pdf");
        try {
            file.transferTo(tempFile);
            return bookService.saveBook(book, tempFile.getPath());
        } catch (Exception e) {
            log.error("Error saving book: {}", e.getMessage());
            throw e;
        } finally {
            // İşlem bitince geçici dosyayı sil
            if (tempFile.exists()) {
                tempFile.delete();
            }
        }
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