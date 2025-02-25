package com.example.book_application.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SavedWordRequest {
    private Long userId;
    private Long bookId;
    private Long wordId;
}
