package com.example.book_application.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.beans.factory.annotation.Autowired;
import com.example.book_application.model.Word;
import com.example.book_application.service.WordService;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.List;

@RestController
@RequestMapping("/api/words")
@RequiredArgsConstructor
@Slf4j
public class WordController {

    @Autowired
    private WordService wordService;

    @GetMapping
    public List<Word> getAllWords() {
        log.info("Fetching all words");
        return wordService.findAllWords();
    }

    // Save a word
    @PostMapping

    public Word createWord(@RequestBody Word word) {
        log.info("Creating word: {}", word.getWordText());
        return wordService.saveWord(word);
    }

    // Get a word by its text
    @GetMapping("/{wordText}")
    public Word getWordByText(@PathVariable String wordText) {
        return wordService.findByWordText(wordText);
    }
}