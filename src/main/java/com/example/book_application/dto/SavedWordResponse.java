package com.example.book_application.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SavedWordResponse {
    private Long id;
    private String englishWord;
    private String turkishWord;
    private Long bookId;
    private String bookTitle;
    private LocalDateTime createdAt;
} 