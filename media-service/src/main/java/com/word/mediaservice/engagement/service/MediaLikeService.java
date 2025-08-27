package com.word.mediaservice.engagement.service;

import com.word.mediaservice.media.dto.MediaMetadataResponseDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import com.word.mediaservice.common.dto.PageResponseDTO;

import java.util.List;

public interface MediaLikeService {
    Mono<Void> likeMedia(String authUserId, String mediaId);
    Mono<Void> unlikeMedia(String authUserId, String mediaId);

    Flux<MediaMetadataResponseDTO> getLikedMedia(String authUserId, int page, int size);
    // Add the new paged method
    Mono<PageResponseDTO<MediaMetadataResponseDTO>> getLikedMediaPaged(String authUserId, int page, int size);
    Mono<List<String>> getLikedMediaIds(String authUserId, List<String> mediaIds);
    Mono<List<String>> getLikedMediaIds(String authUserId);
}
