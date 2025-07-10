package com.word.mediaservice.engagement.service;

import com.word.mediaservice.engagement.exception.DuplicateLikeException;
import com.word.mediaservice.engagement.exception.LikeNotFoundException;
import com.word.mediaservice.engagement.model.MediaLike;
import com.word.mediaservice.media.dto.MediaMetadataResponseDTO;
import com.word.mediaservice.engagement.repository.MediaLikeRepository;
import com.word.mediaservice.media.model.MediaMetadata;
import com.word.mediaservice.media.repository.MediaMetadataRepository;
import com.word.mediaservice.media.util.MediaMetadataMapper;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

import java.time.Instant;
import java.util.List;

@Service
public class MediaLikeServiceImpl implements MediaLikeService{
    private final MediaLikeRepository mediaLikeRepository;
    private final ReactiveMongoTemplate mongoTemplate;
    private final MediaMetadataMapper mediaMetadataMapper;
    private final MediaMetadataRepository mediaMetadataRepository;

    public MediaLikeServiceImpl(MediaLikeRepository mediaLikeRepository, ReactiveMongoTemplate mongoTemplate, MediaMetadataMapper mediaMetadataMapper, MediaMetadataRepository mediaMetadataRepository) {
        this.mediaLikeRepository = mediaLikeRepository;
        this.mongoTemplate = mongoTemplate;
        this.mediaMetadataMapper = mediaMetadataMapper;
        this.mediaMetadataRepository = mediaMetadataRepository;
    }
    private Mono<Void> incrementLikeCount(String mediaId, int delta) {
        Query query = Query.query(Criteria.where("id").is(mediaId));
        Update update = new Update().inc("likeCount", delta);
        return mongoTemplate.updateFirst(query, update, MediaMetadata.class).then();
    }

    @Override
    public Mono<Void> likeMedia(String authUserId, String mediaId) {
        MediaLike like = MediaLike.builder()
                .authUserId(authUserId)
                .mediaId(mediaId)
                .likedAt(Instant.now())
                .build();

        return mediaLikeRepository
                .save(like)
                .then(incrementLikeCount(mediaId, 1))
                .onErrorResume(DuplicateKeyException.class, ex ->
                        Mono.error(new DuplicateLikeException("User has already liked this media."))
                );
    }

    @Override
    public Mono<Void> unlikeMedia(String authUserId, String mediaId) {
        Query query = Query.query(Criteria.where("authUserId").is(authUserId).and("mediaId").is(mediaId));
        return mongoTemplate.remove(query, MediaLike.class)
                .flatMap(result -> {
                    if (result.getDeletedCount() == 0) {
                        return Mono.error(new LikeNotFoundException("Like does not exist."));
                    }
                    return incrementLikeCount(mediaId, -1);
                });
    }

    @Override
    public Flux<MediaMetadataResponseDTO> getLikedMedia(String authUserId, int page, int size) {
        return mediaLikeRepository.findByAuthUserId(authUserId)
                .map(MediaLike::getMediaId)
                .collectList()
                .flatMapMany(allMediaIds -> {
                    int fromIndex = Math.min(page * size, allMediaIds.size());
                    int toIndex = Math.min(fromIndex + size, allMediaIds.size());

                    List<String> pagedIds = allMediaIds.subList(fromIndex, toIndex);
                    return mediaMetadataRepository.findAllById(pagedIds);
                })
                .map(mediaMetadataMapper::toResponseDTO);
    }

    @Override
    public Mono<List<String>> getLikedMediaIds(String authUserId, List<String> mediaIds) {
        return mediaLikeRepository
                .findByAuthUserIdAndMediaIdIn(authUserId, mediaIds)
                .map(MediaLike::getMediaId)
                .collectList();
    }

}
