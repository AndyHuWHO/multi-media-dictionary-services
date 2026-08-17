package com.word.mediaservice.media.service;

import com.word.mediaservice.media.dto.GenerateUploadUrlResponseDTO;
import com.word.mediaservice.media.dto.MediaMetadataRequestDTO;
import com.word.mediaservice.media.dto.MediaMetadataResponseDTO;
import reactor.core.publisher.Mono;

public interface MediaUploadService {
    Mono<GenerateUploadUrlResponseDTO> generatePresignedUrls(String authUserId, String contentType);
    Mono<MediaMetadataResponseDTO> saveMediaMetadata(String authUserId, MediaMetadataRequestDTO requestDTO);
}
