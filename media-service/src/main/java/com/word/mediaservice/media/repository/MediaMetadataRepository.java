package com.word.mediaservice.media.repository;

import com.word.mediaservice.media.model.MediaMetadata;
import com.word.mediaservice.media.model.Visibility;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MediaMetadataRepository extends ReactiveMongoRepository <MediaMetadata, String> {
    Mono<MediaMetadata> findByObjectKey(String objectKey);
    // Find all media uploaded by a specific user
    Flux<MediaMetadata> findByAuthUserId(String authUserId);

    Flux<MediaMetadata> findByAuthUserIdOrderByCreatedAtDesc(String authUserId, Pageable pageable);

    Flux<MediaMetadata> findByAuthUserIdAndVisibilityOrderByCreatedAtDesc(
            String authUserId,
            Visibility visibility,
            Pageable pageable
    );

    Flux<MediaMetadata> findByWordsContainingAndVisibilityOrderByCreatedAtDesc(
            String word,
            Visibility visibility,
            Pageable pageable
    );

    Flux<MediaMetadata> findByVisibility(Visibility visibility, Pageable pageable);
    
    Mono<Long> countByVisibility(Visibility visibility);
    Mono<Long> countByWordsContainingAndVisibility(String word, Visibility visibility);
    Mono<Long> countByAuthUserIdAndVisibility(String authUserId, Visibility visibility);
    Mono<Long> countByAuthUserId(String authUserId);



    // Find all public media
    Flux<MediaMetadata> findByVisibility(Visibility visibility);

    // Find public media associated with a given word
    Flux<MediaMetadata> findByWordsContainingAndVisibility(String word, Visibility visibility);

}
