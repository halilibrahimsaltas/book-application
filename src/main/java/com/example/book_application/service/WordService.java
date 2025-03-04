package com.example.book_application.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.book_application.model.Word;
import com.example.book_application.repository.WordRepository;
import com.example.book_application.core.excepiton.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class WordService {

    @Autowired
    private WordRepository wordRepository;

    public Word saveWord(Word word) {
        log.info("Saving word: {}", word.getWord());
        return wordRepository.save(word);
    }

    public Word findByWord(String word) {
        log.info("Finding word by text: {}", word);
        Word result = wordRepository.findByWord(word);
        if (result == null) {
            log.debug("No word found with text: {}", word);
        }
        return result;
    }

    public List<Word> findAllWords() {
        return wordRepository.findAll();
    }

    public Word findById(Long id) {
        return wordRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Word not found with id: " + id));
    }

    public Word updateWord(Long id, Word wordDetails) {
        Word word = findById(id);
        word.setWord(wordDetails.getWord());
        word.setType(wordDetails.getType());
        word.setCategory(wordDetails.getCategory());
        word.setTr(wordDetails.getTr());
        return wordRepository.save(word);
    }

    public void deleteWord(Long id) {
        Word word = findById(id);
        wordRepository.delete(word);
    }
}