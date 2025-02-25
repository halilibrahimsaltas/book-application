package com.example.book_application.controller;

import org.springframework.web.bind.annotation.*;
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

    @PostMapping
    public Word createWord(@RequestBody Word word) {
        log.info("Creating word: {}", word.getWordText());
        return wordService.saveWord(word);
    }

    @GetMapping("/text/{wordText}")
    public Word getWordByText(@PathVariable String wordText) {
        log.info("Fetching word by text: {}", wordText);
        return wordService.findByWordText(wordText);
    }

    @GetMapping("/{id}")
    public Word getWordById(@PathVariable Long id) {
        log.info("Fetching word with ID: {}", id);
        return wordService.findById(id);
    }

    @PutMapping("/{id}")
    public Word updateWord(@PathVariable Long id, @RequestBody Word word) {
        log.info("Updating word with ID: {}", id);
        return wordService.updateWord(id, word);
    }

    @DeleteMapping("/{id}")
    public void deleteWord(@PathVariable Long id) {
        log.info("Deleting word with ID: {}", id);
        wordService.deleteWord(id);
    }
}