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
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@Slf4j
public class BookController {

    private final BookService bookService;

    private static final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads/pdfs/";
    private static final String IMAGE_UPLOAD_DIR = System.getProperty("user.dir") + "/uploads/images/";
    private static final Set<String> ALLOWED_IMAGE_TYPES = new HashSet<>(Arrays.asList(
        "image/jpeg", "image/png", "image/jpg"
    ));

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Book createBook(
            @RequestPart("book") Book book,
            @RequestPart("file") MultipartFile file,
            @RequestPart(value = "image", required = false) MultipartFile image) throws IOException {
        
        log.info("Creating book: {}", book.getTitle());
        
        if (file.isEmpty()) {
            throw new IllegalArgumentException("PDF dosyası boş olamaz");
        }

        // PDF dosyasını kaydet
        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) {
            boolean created = uploadDir.mkdirs();
            if (!created) {
                throw new IOException("PDF yükleme dizini oluşturulamadı: " + UPLOAD_DIR);
            }
            log.info("PDF upload directory created: {}", UPLOAD_DIR);
        }

        String pdfFileName = System.currentTimeMillis() + "_" + file.getOriginalFilename().replaceAll("\\s+", "_");
        String pdfFilePath = UPLOAD_DIR + pdfFileName;

        // Resim dosyasını kaydet (varsa)
        String imageFilePath = null;
        if (image != null && !image.isEmpty()) {
            if (!ALLOWED_IMAGE_TYPES.contains(image.getContentType())) {
                throw new IllegalArgumentException("Desteklenmeyen resim formatı. Desteklenen formatlar: JPEG, PNG");
            }

            File imageUploadDir = new File(IMAGE_UPLOAD_DIR);
            if (!imageUploadDir.exists()) {
                boolean created = imageUploadDir.mkdirs();
                if (!created) {
                    throw new IOException("Resim yükleme dizini oluşturulamadı: " + IMAGE_UPLOAD_DIR);
                }
                log.info("Image upload directory created: {}", IMAGE_UPLOAD_DIR);
            }

            String imageFileName = System.currentTimeMillis() + "_" + image.getOriginalFilename().replaceAll("\\s+", "_");
            imageFilePath = IMAGE_UPLOAD_DIR + imageFileName;
            
            File imageFile = new File(imageFilePath);
            image.transferTo(imageFile);
            log.info("Image file saved to: {}", imageFilePath);
        }

        try {
            File pdfFile = new File(pdfFilePath);
            file.transferTo(pdfFile);
            log.info("PDF file saved to: {}", pdfFilePath);
            return bookService.saveBook(book, pdfFilePath, imageFilePath);
        } catch (IOException e) {
            // Hata durumunda yüklenen dosyaları temizle
            try {
                if (imageFilePath != null) {
                    new File(imageFilePath).delete();
                }
                new File(pdfFilePath).delete();
            } catch (Exception ex) {
                log.error("Dosyalar temizlenirken hata oluştu: {}", ex.getMessage());
            }
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

    @PutMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Book> updateBookImage(
            @PathVariable Long id,
            @RequestPart("image") MultipartFile image) throws IOException {
        
        if (image.isEmpty()) {
            throw new IllegalArgumentException("Resim dosyası boş olamaz");
        }

        if (!ALLOWED_IMAGE_TYPES.contains(image.getContentType())) {
            throw new IllegalArgumentException("Desteklenmeyen resim formatı. Desteklenen formatlar: JPEG, PNG");
        }

        Book book = bookService.findById(id);
        
        // Eski resmi sil
        if (book.getImagePath() != null) {
            try {
                new File(book.getImagePath()).delete();
            } catch (Exception e) {
                log.error("Eski resim silinirken hata oluştu: {}", e.getMessage());
            }
        }

        // Yeni resmi kaydet
        String imageFileName = System.currentTimeMillis() + "_" + image.getOriginalFilename().replaceAll("\\s+", "_");
        String imageFilePath = IMAGE_UPLOAD_DIR + imageFileName;
        
        File imageFile = new File(imageFilePath);
        image.transferTo(imageFile);
        
        book.setImagePath(imageFilePath);
        Book updatedBook = bookService.updateBook(id, book);
        
        return ResponseEntity.ok(updatedBook);
    }
}