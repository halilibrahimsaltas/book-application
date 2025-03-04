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

    @Column(nullable = false)
    private String word;

    private String type;
    private String category;

    @Column(nullable = false)
    private String tr;
}