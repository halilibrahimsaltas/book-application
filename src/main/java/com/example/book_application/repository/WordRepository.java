package com.example.book_application.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.book_application.model.Word;  

@Repository
public interface WordRepository extends JpaRepository<Word, Long> {
    Word findByWord(String word);
    List<Word> findByWordContainingIgnoreCase(String query);
} 