package com.word.mediaservice.media.service;

import com.word.mediaservice.common.dto.PageResponseDTO;
import com.word.mediaservice.media.dto.GenerateUploadUrlResponseDTO;
import com.word.mediaservice.media.dto.MediaMetadataRequestDTO;
import com.word.mediaservice.media.dto.MediaMetadataResponseDTO;
import com.word.mediaservice.media.dto.MediaMetadataUpdateRequestDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MediaUploadService {
    Mono<Void> validateMediaMetadata(MediaMetadataRequestDTO dto);
    Mono<GenerateUploadUrlResponseDTO> generatePresignedUrls(String authUserId);
    Mono<MediaMetadataResponseDTO> saveMediaMetadata(String authUserId, MediaMetadataRequestDTO requestDTO);

    Flux<MediaMetadataResponseDTO> getUserMedia(String authUserId, int page, int size);
    Mono<PageResponseDTO<MediaMetadataResponseDTO>> getUserMediaPaged(String authUserId, int page, int size);
    Flux<MediaMetadataResponseDTO> getPublicMediaByUser(String userId, int page, int size);
    Mono<PageResponseDTO<MediaMetadataResponseDTO>> getPublicMediaByUserPaged(String userId, int page, int size);
    Flux<MediaMetadataResponseDTO> getPublicMediaByWord(String word, int page, int size);
    Mono<PageResponseDTO<MediaMetadataResponseDTO>> getPublicMediaByWordPaged(String word, int page, int size);
    Flux<MediaMetadataResponseDTO> getFeed(int page);
    Mono<PageResponseDTO<MediaMetadataResponseDTO>> getFeedPaged(int page);

    Mono<MediaMetadataResponseDTO> updateMediaMetadata(String authUserId,
                                                       String mediaId,
                                                       MediaMetadataUpdateRequestDTO dto);

    Mono<Void> deleteMedia(String authUserId, String mediaId);



}
