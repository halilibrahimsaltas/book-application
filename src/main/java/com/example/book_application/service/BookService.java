package com.example.book_application.service;

import org.springframework.stereotype.Service;
import com.example.book_application.model.Book;
import com.example.book_application.model.User;
import com.example.book_application.repository.BookRepository;
import com.example.book_application.core.excepiton.ResourceNotFoundException;
import com.example.book_application.core.util.PdfExtractor;
import com.example.book_application.model.BookProgress;
import com.example.book_application.repository.BookProgressRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final BookProgressRepository bookProgressRepository;
    private final UserService userService;

    public Book saveBook(Book book, String pdfPath, String imagePath) throws IOException {
        book.setFilePath(pdfPath);
        book.setImagePath(imagePath);
        book.setContent(PdfExtractor.extractTextFromPdf(pdfPath));
        return bookRepository.save(book);
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
        if (bookDetails.getImagePath() != null) {
            book.setImagePath(bookDetails.getImagePath());
        }
        return bookRepository.save(book);
    }

    public void deleteBook(Long id) {
        Book book = findById(id);
        // Dosyaları sil
        if (book.getFilePath() != null) {
            try {
                java.nio.file.Files.deleteIfExists(java.nio.file.Paths.get(book.getFilePath()));
            } catch (IOException e) {
                log.error("PDF dosyası silinirken hata oluştu: {}", e.getMessage());
            }
        }
        if (book.getImagePath() != null) {
            try {
                java.nio.file.Files.deleteIfExists(java.nio.file.Paths.get(book.getImagePath()));
            } catch (IOException e) {
                log.error("Resim dosyası silinirken hata oluştu: {}", e.getMessage());
            }
        }
        bookRepository.delete(book);
    }

    public Map<String, Object> getBookContentWithProgress(Long bookId, Integer requestedPage) {
        try {
            Book book = findById(bookId);
            User currentUser = userService.getCurrentUser();
            
            if (book.getContent() == null || book.getContent().isEmpty()) {
                throw new ResourceNotFoundException("Book content not found for id: " + bookId);
            }

            // Kullanıcının ilerleme kaydını bul veya oluştur
            BookProgress progress = bookProgressRepository
                .findByUserAndBook(currentUser, book)
                .orElseGet(() -> {
                    BookProgress newProgress = new BookProgress();
                    newProgress.setUser(currentUser);
                    newProgress.setBook(book);
                    newProgress.setCurrentPage(1);
                    newProgress.setLastReadAt(LocalDateTime.now());
                    newProgress.setTotalReadingTimeInSeconds(0L);
                    return bookProgressRepository.save(newProgress);
                });

            // Sayfa numarası belirtilmemişse son kaldığı sayfadan devam et
            int page = requestedPage != null ? requestedPage : progress.getCurrentPage();
            int pageSize = 1000;

            // İçeriği sayfalara böl
            String[] paragraphs = book.getContent().split("\n\n");
            int totalParagraphs = paragraphs.length;
            
            // Sayfa sınırlarını kontrol et
            if (page < 1) {
                page = 1;
            }
            int totalPages = (int) Math.ceil((double) totalParagraphs / pageSize);
            if (page > totalPages) {
                page = totalPages;
            }

            int start = (page - 1) * pageSize;
            int end = Math.min(start + pageSize, totalParagraphs);

            // Sayfa içeriğini oluştur
            StringBuilder pageContent = new StringBuilder();
            for (int i = start; i < end; i++) {
                pageContent.append(paragraphs[i]).append("\n\n");
            }

            // İlerlemeyi güncelle
            progress.setCurrentPage(page);
            progress.setLastReadAt(LocalDateTime.now());
            bookProgressRepository.save(progress);

            Map<String, Object> response = new HashMap<>();
            response.put("content", pageContent.toString());
            response.put("currentPage", page);
            response.put("totalPages", totalPages);
            response.put("lastReadAt", progress.getLastReadAt());
            response.put("totalReadingTime", progress.getTotalReadingTimeInSeconds());
            response.put("bookTitle", book.getTitle());
            response.put("bookAuthor", book.getAuthor());
            
            log.info("Retrieved content for book: {}, page: {}/{}", bookId, page, totalPages);
            
            return response;

        } catch (Exception e) {
            log.error("Error getting book content with progress: {}", e.getMessage());
            throw new RuntimeException("Error processing book content: " + e.getMessage());
        }
    }
}