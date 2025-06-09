package com.word.wordservice.repository;

import com.word.wordservice.model.WordEntry;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface WordEntryRepository extends ReactiveMongoRepository <WordEntry, String> {
    Mono<WordEntry> findByWord(String word);
}
