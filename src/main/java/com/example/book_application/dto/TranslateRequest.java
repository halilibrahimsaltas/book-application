package com.example.book_application.dto;

import lombok.Data;

@Data
public class TranslateRequest {
    private String enWord;
    private String trWord;
    private String type;
    private String category;
} 