package com.example.book_application.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SavedWordResponse {
    private Long id;
    private UserDTO user;
    private BookDTO book;
    private WordDTO word;
    private LocalDateTime savedDate;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserDTO {
        private Long id;
        private String username;
        private String email;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookDTO {
        private Long id;
        private String title;
        private String author;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WordDTO {
        private String enWord;
        private String trWord;
        private String type;
        private String category;
    }
} 