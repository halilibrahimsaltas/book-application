package com.example.book_application.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.book_application.model.Turkish;

@Repository
public interface TRRepository extends JpaRepository<Turkish, Long> {
    Turkish findByWord(String word);
    boolean existsByWord(String word);
} 