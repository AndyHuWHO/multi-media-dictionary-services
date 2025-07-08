package com.word.mediaservice.media.controller;

import com.word.mediaservice.media.dto.GenerateUploadUrlResponseDTO;
import com.word.mediaservice.media.dto.MediaMetadataRequestDTO;
import com.word.mediaservice.media.dto.MediaMetadataResponseDTO;
import com.word.mediaservice.media.dto.MediaMetadataSaveResponseDTO;
import com.word.mediaservice.media.service.MediaUploadService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/media")
public class MediaController {
    private final MediaUploadService mediaUploadService;

    public MediaController(MediaUploadService mediaUploadService) {
        this.mediaUploadService = mediaUploadService;
    }

    @PostMapping("/validate")
    public Mono<ResponseEntity<Void>> validateMetadata(
            @Valid @RequestBody MediaMetadataRequestDTO requestDTO
    ) {
        // If validation passes, just return 204 No Content
        return mediaUploadService.validateMediaMetadata(requestDTO)
                .thenReturn(ResponseEntity.noContent().build());
    }

    @PostMapping("/upload-url")
    public Mono<ResponseEntity<GenerateUploadUrlResponseDTO>> generateUploadUrls(
            @RequestHeader("X-Auth-UserId") String userId
    ) {
        return mediaUploadService.generatePresignedUrls(userId)
                .map(ResponseEntity::ok);
    }

    @PostMapping
    public Mono<ResponseEntity<MediaMetadataSaveResponseDTO>> saveMediaMetadata(
            @RequestHeader("X-Auth-UserId") String authUserId,
            @Valid @RequestBody MediaMetadataRequestDTO requestDTO
    ) {
        return mediaUploadService.saveMediaMetadata(authUserId, requestDTO)
                .map(ResponseEntity::ok);
    }
}
