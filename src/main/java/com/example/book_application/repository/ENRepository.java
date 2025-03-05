package com.example.book_application.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.example.book_application.model.English;
import java.util.List;

@Repository
public interface ENRepository extends JpaRepository<English, Long> {
    English findByWord(String word);
    
    @Query("SELECT e FROM English e WHERE LOWER(e.word) LIKE LOWER(CONCAT('%', :word, '%'))")
    List<English> findByWordContainingIgnoreCase(@Param("word") String word);
} 