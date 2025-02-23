package com.example.book_application.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.book_application.model.Word;  

@Repository
public interface WordRepository extends JpaRepository<Word, Long> {
    Word findByWordText(String wordText);
    List<Word> findByWordTextContainingIgnoreCase(String query);
} 