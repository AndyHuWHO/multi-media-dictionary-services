package com.word.mediaservice.media.service;

import com.word.mediaservice.media.dto.GenerateUploadUrlResponseDTO;
import com.word.mediaservice.media.dto.MediaMetadataRequestDTO;
import com.word.mediaservice.media.dto.MediaMetadataResponseDTO;
import com.word.mediaservice.media.dto.MediaMetadataSaveResponseDTO;
import reactor.core.publisher.Mono;

public interface MediaUploadService {
    Mono<Void> validateMediaMetadata(MediaMetadataRequestDTO dto);
    Mono<GenerateUploadUrlResponseDTO> generatePresignedUrls(String authUserId);
    Mono<MediaMetadataSaveResponseDTO> saveMediaMetadata(String authUserId, MediaMetadataRequestDTO requestDTO);

}
