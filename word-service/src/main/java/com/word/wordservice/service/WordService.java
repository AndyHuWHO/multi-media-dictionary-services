package com.word.wordservice.service;

import com.word.wordservice.model.WordEntry;
import reactor.core.publisher.Mono;

public interface WordService {
    Mono<WordEntry> lookupWord(String word);
}
