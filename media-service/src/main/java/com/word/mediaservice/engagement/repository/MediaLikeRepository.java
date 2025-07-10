package com.word.mediaservice.engagement.repository;

import com.mongodb.client.result.DeleteResult;
import com.word.mediaservice.engagement.model.MediaLike;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface MediaLikeRepository extends ReactiveMongoRepository<MediaLike, String> {
    Mono<Boolean> existsByAuthUserIdAndMediaId(String authUserId, String mediaId);

    Flux<MediaLike> findByAuthUserIdAndMediaIdIn(String authUserId, List<String> mediaIds);
    
    Mono<Void> deleteByAuthUserIdAndMediaId(String authUserId, String mediaId);

    Flux<MediaLike> findByMediaId(String mediaId);

    Flux<MediaLike> findByAuthUserId(String authUserId);
}
