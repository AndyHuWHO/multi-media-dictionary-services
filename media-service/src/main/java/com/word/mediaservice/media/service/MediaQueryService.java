package com.word.mediaservice.media.service;

import com.word.mediaservice.common.dto.PageResponseDTO;
import com.word.mediaservice.media.dto.MediaMetadataResponseDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MediaQueryService {
    Flux<MediaMetadataResponseDTO> getUserMedia(String authUserId, int page, int size);
    Mono<PageResponseDTO<MediaMetadataResponseDTO>> getUserMediaPaged(String authUserId, int page, int size);
    Flux<MediaMetadataResponseDTO> getPublicMediaByUser(String userId, int page, int size);
    Mono<PageResponseDTO<MediaMetadataResponseDTO>> getPublicMediaByUserPaged(String userId, int page, int size);
    Mono<PageResponseDTO<MediaMetadataResponseDTO>> getPublicMediaByWordPaged(String word, int page, int size);
    Mono<PageResponseDTO<MediaMetadataResponseDTO>> getFeedPaged(int page);
}
