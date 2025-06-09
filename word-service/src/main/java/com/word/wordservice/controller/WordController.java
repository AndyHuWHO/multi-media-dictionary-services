package com.word.wordservice.controller;

import com.word.wordservice.model.WordEntry;
import com.word.wordservice.service.WordService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/words")
public class WordController {
    private final WordService wordService;

    public WordController(WordService wordService) {
        this.wordService = wordService;
    }

    @GetMapping("")
    public String welcome() {
        return "Welcome to MVP word service api!";
    }

    @GetMapping("/{word}")
    public Mono<WordEntry> lookupWord(@PathVariable String word) {
        return wordService.lookupWord(word);
    }
}
