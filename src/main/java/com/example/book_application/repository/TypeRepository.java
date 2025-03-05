package com.example.book_application.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.book_application.model.Type;

@Repository
public interface TypeRepository extends JpaRepository<Type, Long> {
    Type findByName(String name);
    boolean existsByName(String name);
} 