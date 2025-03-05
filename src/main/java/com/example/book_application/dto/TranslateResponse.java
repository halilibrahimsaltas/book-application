package com.example.book_application.dto;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TranslateResponse {
    private Long id;
    private String enWord;
    private String trWord;
    private String type;
    private String category;
} 