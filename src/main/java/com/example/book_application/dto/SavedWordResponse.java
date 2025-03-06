package com.example.book_application.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SavedWordResponse {
    private Long id;
    private String englishWord;
    private String turkishWord;
    private LocalDateTime savedDate;
} 