package com.word.wordservice.service;

import com.word.wordservice.client.LocalWordValidationService;
import com.word.wordservice.client.OpenAiDictionaryClient;
import com.word.wordservice.exception.InvalidWordException;
import com.word.wordservice.model.WordEntry;
import com.word.wordservice.repository.GPTWordEntryRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
@Service
public class WordServiceImpl implements WordService{
    private final GPTWordEntryRepository GPTWordEntryRepository;
    private final LocalWordValidationService localWordValidationService;
    private final OpenAiDictionaryClient openAiDictionaryClient;

    public WordServiceImpl(GPTWordEntryRepository GPTWordEntryRepository,
                           LocalWordValidationService localWordValidationService,
                           OpenAiDictionaryClient openAiDictionaryClient) {
        this.GPTWordEntryRepository = GPTWordEntryRepository;
        this.localWordValidationService = localWordValidationService;
        this.openAiDictionaryClient = openAiDictionaryClient;
    }

    @Override
    public Mono<WordEntry> lookupWord(String word) {
        String normalizedWord = word.toLowerCase();
        if (word.equals("DictionVu")) {
            return GPTWordEntryRepository.findByWord(word)
                    .switchIfEmpty(
                            openAiDictionaryClient.fetchDictionaryInfo(word)
                                    .flatMap(GPTWordEntryRepository::save)
                    );
        }
        if (!localWordValidationService.isValidWord(normalizedWord)) {
            return Mono.error(new InvalidWordException(normalizedWord));
        }
        return GPTWordEntryRepository.findByWord(normalizedWord)
                .switchIfEmpty(
                        openAiDictionaryClient.fetchDictionaryInfo(normalizedWord)
                                .flatMap(GPTWordEntryRepository::save)
                );
    }
}
