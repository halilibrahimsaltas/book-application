package com.example.book_application.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "words")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Word {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

  
    @Column(name = "word_text", nullable = false)
    private String wordText;

   
    @Column(nullable = false)
    private String language;

    @JsonManagedReference
    @OneToMany(mappedBy = "word", cascade = CascadeType.ALL)
    private List<Translation> translations = new ArrayList<>();

    @JsonManagedReference
    @OneToMany(mappedBy = "word", cascade = CascadeType.ALL)
    private List<Pronunciation> pronunciations = new ArrayList<>();
}