package com.example.book_application.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "english")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class English {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String word;
}