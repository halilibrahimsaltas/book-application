package com.example.book_application.repository;

import com.example.book_application.dto.TranslateResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.example.book_application.model.Translate;
import com.example.book_application.model.English;
import com.example.book_application.model.Turkish;  
import java.util.List;

@Repository
public interface TranslateRepository extends JpaRepository<Translate, Long> {
    List<Translate> findByEnglish(English english);
    List<Translate> findByTurkish(Turkish turkish);
    boolean existsByEnglishAndTurkish(English english, Turkish turkish);

    @Query(value = "SELECT new com.example.book_application.dto.TranslateResponse(" +
            "t.id, e.word, tr.word, COALESCE(ty.name, ''), COALESCE(c.name, '')) " +
            "FROM Translate t " +
            "INNER JOIN t.english e " +
            "INNER JOIN t.turkish tr " +
            "LEFT JOIN t.type ty " +
            "LEFT JOIN t.category c " +
            "WHERE LOWER(e.word) = LOWER(:word)")
    List<TranslateResponse> findTranslationsByWord(@Param("word") String word);
} 