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
import com.word.mediaservice.common.dto.PageResponseDTO;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

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
        return mediaLikeRepository.findByAuthUserIdOrderByLikedAtDesc(authUserId)
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
    public Mono<PageResponseDTO<MediaMetadataResponseDTO>> getLikedMediaPaged(String authUserId, int page, int size) {
        Mono<Long> totalMono = mediaLikeRepository.countByAuthUserId(authUserId);

        Mono<List<String>> allIdsMono = mediaLikeRepository.findByAuthUserIdOrderByLikedAtDesc(authUserId)
                .map(MediaLike::getMediaId)
                .collectList();

        // Combine count and IDs, extract page, fetch and build response
        return Mono.zip(totalMono, allIdsMono)
                .flatMap(tuple -> {
                    long total = tuple.getT1();
                    List<String> allIds = tuple.getT2();

                    int fromIndex = Math.min(page * size, allIds.size());
                    int toIndex   = Math.min(fromIndex + size, allIds.size());
                    List<String> pageIds = allIds.subList(fromIndex, toIndex);

                    return mediaMetadataRepository.findAllById(pageIds)
                            .collectList()
                            .map(metadatas -> {
                                Map<String, MediaMetadata> metadataById = metadatas.stream()
                                        .collect(Collectors.toMap(MediaMetadata::getId, Function.identity()));
                                List<MediaMetadataResponseDTO> orderedContent = pageIds.stream()
                                        .map(metadataById::get)
                                        .filter(Objects::nonNull)
                                        .map(mediaMetadataMapper::toResponseDTO)
                                        .toList();

                                int totalPages = (int) Math.ceil((double) total / size);

                                return PageResponseDTO.<MediaMetadataResponseDTO>builder()
                                        .content(orderedContent)
                                        .page(page)
                                        .pageSize(size)
                                        .totalPages(totalPages)
                                        .build();
                            });
                });
    }



    @Override
    public Mono<List<String>> getLikedMediaIds(String authUserId, List<String> mediaIds) {
        return mediaLikeRepository
                .findByAuthUserIdAndMediaIdIn(authUserId, mediaIds)
                .map(MediaLike::getMediaId)
                .collectList();
    }

    @Override
    public Mono<List<String>> getLikedMediaIds(String authUserId) {
        return mediaLikeRepository.findByAuthUserIdOrderByLikedAtDesc(authUserId)
                .map(MediaLike::getMediaId)
                .collectList();
    }

}
