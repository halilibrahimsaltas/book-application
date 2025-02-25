package com.example.book_application.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SavedWordResponse {
    private Long id;
    private UserDTO user;
    private BookDTO book;
    private WordDTO word;
    private LocalDateTime savedDate;

    @Data
    public static class UserDTO {
        private Long id;
        private String username;
        private String email;
    }

    @Data
    public static class BookDTO {
        private Long id;
        private String title;
        private String author;
    }

    @Data
    public static class WordDTO {
        private Long id;
        private String wordText;
        private String language;
    }
} 