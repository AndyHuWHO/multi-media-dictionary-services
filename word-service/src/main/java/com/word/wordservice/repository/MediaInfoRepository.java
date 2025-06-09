package com.word.wordservice.repository;

import com.word.wordservice.model.MediaInfo;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface MediaInfoRepository extends ReactiveMongoRepository <MediaInfo, String> {
    Flux<MediaInfo> findByWord(String word);
    Flux<MediaInfo> findByWordAndIsPublicTrue(String word);
    Flux<MediaInfo> findByUploadedBy(String uploadedBy);

}
