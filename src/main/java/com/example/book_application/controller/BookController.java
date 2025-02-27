package com.example.book_application.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@Slf4j
public class BookController {

    private final BookService bookService;

    private static final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads/pdfs/";

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Book createBook(
            @RequestPart("book") Book book,
            @RequestPart("file") MultipartFile file) throws IOException {
        
        log.info("Creating book: {}", book.getTitle());
        
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Dosya boş olamaz");
        }
        
        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) {
            boolean created = uploadDir.mkdirs();
            if (!created) {
                throw new IOException("Yükleme dizini oluşturulamadı: " + UPLOAD_DIR);
            }
            log.info("Upload directory created: {}", UPLOAD_DIR);
        }

        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename().replaceAll("\\s+", "_");
        String filePath = UPLOAD_DIR + fileName;

        File dest = new File(filePath);
        try {
            file.transferTo(dest);
            log.info("PDF file saved to: {}", filePath);
            return bookService.saveBook(book, filePath);
        } catch (IOException e) {
            log.error("Dosya kaydedilirken hata oluştu: {}", e.getMessage());
            throw new IOException("Dosya kaydedilemedi: " + e.getMessage());
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

    @GetMapping("/{id}/content")
    public ResponseEntity<Map<String, Object>> getBookContent(
            @PathVariable Long id,
            @RequestParam(required = false) Integer page) {
        try {
            Map<String, Object> response = bookService.getBookContentWithProgress(id, page);
            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(404).body(errorResponse);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error processing book content: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}