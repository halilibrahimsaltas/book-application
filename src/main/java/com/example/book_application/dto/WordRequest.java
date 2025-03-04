package com.example.book_application.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WordRequest {
    private String word;
    private String type;
    private String category;
    private String tr;
} 