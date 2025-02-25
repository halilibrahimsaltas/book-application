package com.example.book_application.dto;

import lombok.Data;

@Data
public class PronunciationRequest {
    private String audioUrl;
    private String language;
    private Long wordId;
} 