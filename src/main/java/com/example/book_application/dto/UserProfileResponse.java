package com.example.book_application.dto;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {
    private Long id;
    private String username;
    private String email;
    private LocalDateTime createdAt;
    private Integer totalBooks;
    private Integer totalSavedWords;
    private Long totalReadingTime;
} 