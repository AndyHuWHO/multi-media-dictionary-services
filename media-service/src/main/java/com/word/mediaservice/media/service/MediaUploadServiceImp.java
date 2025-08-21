package com.word.mediaservice.media.service;

import com.word.mediaservice.common.dto.PageResponseDTO;
import com.word.mediaservice.media.dto.GenerateUploadUrlResponseDTO;
import com.word.mediaservice.media.dto.MediaMetadataRequestDTO;
import com.word.mediaservice.media.dto.MediaMetadataResponseDTO;
import com.word.mediaservice.media.dto.MediaMetadataUpdateRequestDTO;
import com.word.mediaservice.media.model.MediaMetadata;
import com.word.mediaservice.media.model.Visibility;
import com.word.mediaservice.media.repository.MediaMetadataRepository;
import com.word.mediaservice.media.util.MediaMetadataMapper;
import com.word.mediaservice.media.util.S3Util;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Random;

@Service
public class MediaUploadServiceImp implements MediaUploadService {
    private final S3Util s3Util;
    private final MediaMetadataRepository mediaMetadataRepository;
    private final MediaMetadataMapper mediaMetadataMapper;

    public MediaUploadServiceImp(S3Util s3Util, MediaMetadataRepository mediaMetadataRepository, MediaMetadataMapper mediaMetadataMapper) {
        this.s3Util = s3Util;
        this.mediaMetadataRepository = mediaMetadataRepository;
        this.mediaMetadataMapper = mediaMetadataMapper;
    }

    @Override
    public Mono<Void> validateMediaMetadata(MediaMetadataRequestDTO dto) {
        return mediaMetadataRepository.findByObjectKey(dto.getObjectKey())
                .flatMap(existing -> Mono.error(
                        new IllegalArgumentException("Duplicate objectKey: " +
                                "already used. Please request a new upload URL.")))
                .then();
    }

    @Override
    public Mono<GenerateUploadUrlResponseDTO> generatePresignedUrls(String authUserId) {
        String videoKey = s3Util.buildVideoKey(authUserId);
        String thumbnailKey = s3Util.buildThumbnailKey(authUserId);

        String videoUploadUrl = s3Util.generateVideoUploadUrl(videoKey);
        String thumbnailUploadUrl = s3Util.generateThumbnailUploadUrl(thumbnailKey);

        GenerateUploadUrlResponseDTO response = GenerateUploadUrlResponseDTO.builder()
                .uploadUrl(videoUploadUrl)
                .objectKey(videoKey)
                .thumbnailUploadUrl(thumbnailUploadUrl)
                .thumbnailKey(thumbnailKey)
                .build();
        return Mono.just(response);
    }

    @Override
    public Mono<MediaMetadataResponseDTO> saveMediaMetadata(String authUserId, MediaMetadataRequestDTO dto) {
        MediaMetadata metadata = MediaMetadata.builder()
                .authUserId(authUserId)
                .objectKey(dto.getObjectKey())
                .thumbnailKey(dto.getThumbnailKey())
                .description(dto.getDescription())
                .words(dto.getWords())
                .tags(dto.getTags())
                .durationSeconds(dto.getDurationSeconds())
                .fileSizeBytes(dto.getFileSizeBytes())
                .visibility(dto.getVisibility())
                .likeCount(0)
                .commentCount(0)
                .build();
        return mediaMetadataRepository.save(metadata)
                .map(mediaMetadataMapper::toResponseDTO);
    }


    @Override
    public Flux<MediaMetadataResponseDTO> getUserMedia(String authUserId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return mediaMetadataRepository.findByAuthUserIdOrderByCreatedAtDesc(authUserId, pageable)
                .map(mediaMetadataMapper::toResponseDTO);
    }

    @Override
    public Mono<PageResponseDTO<MediaMetadataResponseDTO>> getUserMediaPaged(String authUserId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Mono<Long> totalMono = mediaMetadataRepository.countByAuthUserId(authUserId);
        Flux<MediaMetadata> contentFlux = mediaMetadataRepository.findByAuthUserIdOrderByCreatedAtDesc(authUserId, pageable);

        return producePageResponse(contentFlux, totalMono, page, size);
    }

    @Override
    public Flux<MediaMetadataResponseDTO> getPublicMediaByUser(String userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return mediaMetadataRepository
                .findByAuthUserIdAndVisibilityOrderByCreatedAtDesc(userId, Visibility.PUBLIC, pageable)
                .map(mediaMetadataMapper::toResponseDTO);
    }

    @Override
    public Mono<PageResponseDTO<MediaMetadataResponseDTO>> getPublicMediaByUserPaged(String userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Mono<Long> totalMono = mediaMetadataRepository.countByAuthUserIdAndVisibility(userId, Visibility.PUBLIC);
        Flux<MediaMetadata> contentFlux = mediaMetadataRepository
                .findByAuthUserIdAndVisibilityOrderByCreatedAtDesc(userId, Visibility.PUBLIC, pageable);
        return producePageResponse(contentFlux, totalMono, page, size);
    }

    @Override
    public Flux<MediaMetadataResponseDTO> getPublicMediaByWord(String word, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return mediaMetadataRepository
                .findByWordsContainingAndVisibilityOrderByCreatedAtDesc(word, Visibility.PUBLIC, pageable)
                .map(mediaMetadataMapper::toResponseDTO);
    }

    @Override
    public Mono<PageResponseDTO<MediaMetadataResponseDTO>> getPublicMediaByWordPaged(String word, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Mono<Long> totalMono = mediaMetadataRepository.countByWordsContainingAndVisibility(word, Visibility.PUBLIC);
        Flux<MediaMetadata> contentFlux = mediaMetadataRepository
                .findByWordsContainingAndVisibilityOrderByCreatedAtDesc(word, Visibility.PUBLIC, pageable);

        return producePageResponse(contentFlux, totalMono, page, size);

    }

    @Override
    public Flux<MediaMetadataResponseDTO> getFeed(int page) {
        int recentSize = 10;
        int likedSize = 5;
        int commentedSize = 5;

        Pageable recentPage = PageRequest.of(page, recentSize, Sort.by(Sort.Direction.DESC, "createdAt"));
        Pageable likedPage = PageRequest.of(page, likedSize, Sort.by(Sort.Direction.DESC, "likeCount"));
        Pageable commentedPage = PageRequest.of(page, commentedSize, Sort.by(Sort.Direction.DESC, "commentCount"));

        Flux<MediaMetadata> recent = mediaMetadataRepository.findByVisibility(Visibility.PUBLIC, recentPage);
        Flux<MediaMetadata> liked = mediaMetadataRepository.findByVisibility(Visibility.PUBLIC, likedPage);
        Flux<MediaMetadata> commented = mediaMetadataRepository.findByVisibility(Visibility.PUBLIC, commentedPage);

        return Flux.merge(recent, liked, commented)
                .distinct(MediaMetadata::getId)  // deduplication
                .map(mediaMetadataMapper::toResponseDTO);       // add presigned GET URLs
    }

    @Override
    public Mono<PageResponseDTO<MediaMetadataResponseDTO>> getFeedPaged(int page) {
        int globalFetchSize = 200;
        int pageSize = 20;

        Flux<MediaMetadata> recent = mediaMetadataRepository.findByVisibility(
                Visibility.PUBLIC, PageRequest.of(0, globalFetchSize, Sort.by(Sort.Direction.DESC, "createdAt"))
        );
        Flux<MediaMetadata> liked = mediaMetadataRepository.findByVisibility(
                Visibility.PUBLIC, PageRequest.of(0, globalFetchSize, Sort.by(Sort.Direction.DESC, "likeCount"))
        );
        Flux<MediaMetadata> commented = mediaMetadataRepository.findByVisibility(
                Visibility.PUBLIC, PageRequest.of(0, globalFetchSize, Sort.by(Sort.Direction.DESC, "commentCount"))
        );

        Mono<List<MediaMetadataResponseDTO>> mergedMono = Flux
                .merge(recent, liked, commented)
                .distinct(MediaMetadata::getId)
                .map(mediaMetadataMapper::toResponseDTO)
                .collectList()
                .map(list -> {
                    long fixedSeed = 123;
                    Collections.shuffle(list, new Random(fixedSeed));
                    return list;
                });

        return mergedMono.map(content -> {
            int fromIndex = page * pageSize;
            int toIndex = Math.min(fromIndex + pageSize, content.size());
            List<MediaMetadataResponseDTO> pageContent = fromIndex < toIndex ? content.subList(fromIndex, toIndex) : List.of();

            int totalPages = (int) Math.ceil((double) content.size() / pageSize);

            return PageResponseDTO.<MediaMetadataResponseDTO>builder()
                    .content(pageContent)
                    .page(page)
                    .pageSize(pageSize)
                    .totalPages(totalPages)
                    .build();
        });

    }

    @Override
    public Mono<MediaMetadataResponseDTO> updateMediaMetadata(String authUserId,
                                                              String mediaId,
                                                              MediaMetadataUpdateRequestDTO dto) {
        return mediaMetadataRepository.findById(mediaId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Media not found.")))
                .flatMap(media -> {
                    if (!media.getAuthUserId().equals(authUserId)) {
                        return Mono.error(new IllegalArgumentException("Update failed. " +
                                "You are not the owner of this media."));
                    }
                    if (dto.getDescription() != null) {
                        media.setDescription(dto.getDescription());
                    }
                    if (dto.getTags() != null) {
                        media.setTags(dto.getTags());
                    }
                    if (dto.getWords() != null) {
                        media.setWords(dto.getWords());
                    }
                    if (dto.getVisibility() != null) {
                        media.setVisibility(dto.getVisibility());
                    }

                    return mediaMetadataRepository.save(media)
                            .map(mediaMetadataMapper::toResponseDTO);
                });
    }

    @Override
    public Mono<Void> deleteMedia(String authUserId, String mediaId) {
        return mediaMetadataRepository.findById(mediaId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Media not found.")))
                .flatMap(media -> {
                    if (!media.getAuthUserId().equals(authUserId)) {
                        return Mono.error(new IllegalArgumentException("Delete failed. " +
                                "You are not the owner of this media."));
                    }

                    String videoKey = media.getObjectKey();
                    String thumbnailKey = media.getThumbnailKey();

                    return s3Util.s3AsyncDelete(videoKey)
                            .then(s3Util.s3AsyncDelete(thumbnailKey))
                            .then(mediaMetadataRepository.delete(media));
                });
    }


    private Mono<PageResponseDTO<MediaMetadataResponseDTO>> producePageResponse(Flux<MediaMetadata> contentFlux,
                                                                                Mono<Long> totalMono,
                                                                                int page,
                                                                                int size) {
        return Mono.zip(
                totalMono,
                contentFlux.map(mediaMetadataMapper::toResponseDTO).collectList()
        ).map(tuple -> {
            long total = tuple.getT1();
            List<MediaMetadataResponseDTO> content = tuple.getT2();
            int totalPages = (int) Math.ceil((double) total / size);
            return PageResponseDTO.<MediaMetadataResponseDTO>builder()
                    .content(content)
                    .page(page)
                    .pageSize(size)
                    .totalPages(totalPages)
                    .build();
        });
    }

}
