package com.example.book_application.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "translate")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Translate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "english_id")
    private English english;

    @ManyToOne
    @JoinColumn(name = "turkish_id")
    private Turkish turkish;

    @ManyToOne
    @JoinColumn(name = "type_id")
    private Type type;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
}