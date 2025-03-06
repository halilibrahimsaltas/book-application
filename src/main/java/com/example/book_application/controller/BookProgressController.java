package com.example.book_application.controller;

import com.example.book_application.model.BookProgress;
import com.example.book_application.service.BookProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookProgressController {

    private final BookProgressService bookProgressService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{bookId}/progress")
    public ResponseEntity<BookProgress> getProgress(@PathVariable Long bookId) {
        return ResponseEntity.ok(bookProgressService.getProgress(bookId));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{bookId}/progress")
    public ResponseEntity<BookProgress> saveProgress(
            @PathVariable Long bookId,
            @RequestBody BookProgress progress) {
        return ResponseEntity.ok(bookProgressService.saveProgress(bookId, progress));
    }
} 