package com.example.book_application.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.book_application.model.Word;
import com.example.book_application.service.WordService;
import com.example.book_application.dto.WordRequest;

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
    public Word createWord(@RequestBody WordRequest request) {
        log.info("Creating word: {}", request.getWord());
        Word word = new Word();
        word.setWord(request.getWord());
        word.setType(request.getType());
        word.setCategory(request.getCategory());
        word.setTr(request.getTr());
        return wordService.saveWord(word);
    }

    @GetMapping("/search/{word}")
    public Word getWordByText(@PathVariable String word) {
        log.info("Fetching word by text: {}", word);
        return wordService.findByWord(word);
    }

    @GetMapping("/{id}")
    public Word getWordById(@PathVariable Long id) {
        log.info("Fetching word with ID: {}", id);
        return wordService.findById(id);
    }

    @PutMapping("/{id}")
    public Word updateWord(@PathVariable Long id, @RequestBody WordRequest request) {
        log.info("Updating word with ID: {}", id);
        Word word = new Word();
        word.setWord(request.getWord());
        word.setType(request.getType());
        word.setCategory(request.getCategory());
        word.setTr(request.getTr());
        return wordService.updateWord(id, word);
    }

    @DeleteMapping("/{id}")
    public void deleteWord(@PathVariable Long id) {
        log.info("Deleting word with ID: {}", id);
        wordService.deleteWord(id);
    }
}