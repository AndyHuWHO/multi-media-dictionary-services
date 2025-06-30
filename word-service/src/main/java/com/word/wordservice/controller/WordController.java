package com.word.wordservice.controller;

import com.word.wordservice.model.WordEntry;
import com.word.wordservice.service.WordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/words")
@Tag(name = "Word Service", description = "Word Service API")
public class WordController {
    private final WordService wordService;

    public WordController(WordService wordService) {
        this.wordService = wordService;
    }

    @GetMapping("")
    @Operation(summary = "Welcome to MVP word service api!")
    public String welcome() {
        return "Welcome to MVP word service api!";
    }

    @GetMapping("/{word}")
    @Operation(summary = "Lookup word")
    public Mono<WordEntry> lookupWord(@PathVariable String word) {
        return wordService.lookupWord(word);
    }
}
