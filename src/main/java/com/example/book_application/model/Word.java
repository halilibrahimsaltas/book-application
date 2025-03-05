package com.example.book_application.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "english_words", indexes = {
    @Index(name = "idx_word", columnList = "word")
})
public class Word {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String word;

    @Column(length = 50)
    private String type;

    @Column(length = 50)
    private String category;

    @Column(nullable = false, length = 255)
    private String tr;
}