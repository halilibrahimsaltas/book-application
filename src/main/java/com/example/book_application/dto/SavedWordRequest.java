package com.example.book_application.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SavedWordRequest {
    private Long bookId;
    private String englishWord;
    private String turkishWord;
}
