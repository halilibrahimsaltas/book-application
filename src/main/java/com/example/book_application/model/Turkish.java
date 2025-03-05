package com.example.book_application.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "turkish")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Turkish {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String word;
}