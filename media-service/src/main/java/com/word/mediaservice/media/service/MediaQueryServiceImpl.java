package com.word.mediaservice.media.service;

import com.word.mediaservice.common.dto.PageResponseDTO;
import com.word.mediaservice.media.dto.MediaMetadataResponseDTO;
import com.word.mediaservice.media.model.MediaMetadata;
import com.word.mediaservice.media.model.Visibility;
import com.word.mediaservice.media.repository.MediaMetadataRepository;
import com.word.mediaservice.media.util.MediaMetadataMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

@Service
public class MediaQueryServiceImpl implements MediaQueryService {
    private final MediaMetadataRepository mediaMetadataRepository;
    private final MediaMetadataMapper mediaMetadataMapper;

    public MediaQueryServiceImpl(MediaMetadataRepository mediaMetadataRepository,
                                 MediaMetadataMapper mediaMetadataMapper) {
        this.mediaMetadataRepository = mediaMetadataRepository;
        this.mediaMetadataMapper = mediaMetadataMapper;
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
    public Mono<PageResponseDTO<MediaMetadataResponseDTO>> getPublicMediaByWordPaged(String word, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Mono<Long> totalMono = mediaMetadataRepository.countByWordsContainingAndVisibility(word, Visibility.PUBLIC);
        Flux<MediaMetadata> contentFlux = mediaMetadataRepository
                .findByWordsContainingAndVisibilityOrderByCreatedAtDesc(word, Visibility.PUBLIC, pageable);

        return producePageResponse(contentFlux, totalMono, page, size);
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
                    // Guarantee deterministic order before shuffle
                    list.sort(Comparator.comparing(MediaMetadataResponseDTO::getId));
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
