package com.word.wordservice.service;

import com.word.wordservice.client.LocalWordValidationService;
import com.word.wordservice.client.OpenAiDictionaryClient;
import com.word.wordservice.exception.InvalidWordException;
import com.word.wordservice.model.WordEntry;
import com.word.wordservice.repository.MediaInfoRepository;
import com.word.wordservice.repository.WordEntryRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
@Service
public class WordServiceImpl implements WordService{
    private final WordEntryRepository wordEntryRepository;
    private final MediaInfoRepository mediaInfoRepository;
    private final LocalWordValidationService localWordValidationService;
    private final OpenAiDictionaryClient openAiDictionaryClient;

    public WordServiceImpl(WordEntryRepository wordEntryRepository,
                           MediaInfoRepository mediaInfoRepository,
                           LocalWordValidationService localWordValidationService,
                           OpenAiDictionaryClient openAiDictionaryClient) {
        this.wordEntryRepository = wordEntryRepository;
        this.mediaInfoRepository = mediaInfoRepository;
        this.localWordValidationService = localWordValidationService;
        this.openAiDictionaryClient = openAiDictionaryClient;
    }

    @Override
    public Mono<WordEntry> lookupWord(String word) {
        String normalizedWord = word.toLowerCase();
        if (!localWordValidationService.isValidWord(normalizedWord)) {
            return Mono.error(new InvalidWordException(normalizedWord));
        }
        return wordEntryRepository.findByWord(normalizedWord)
                .switchIfEmpty(
                        openAiDictionaryClient.fetchDictionaryInfo(normalizedWord)
                                .flatMap(wordEntryRepository::save)
                )
                .flatMap(wordEntry ->
                        mediaInfoRepository.findByWordAndIsPublicTrue(normalizedWord)
                                .collectList()
                                .map(mediaInfoList ->{
                                    wordEntry.setMediaInfoList(mediaInfoList);
                                    return wordEntry;
                                })
                );
    }
}
