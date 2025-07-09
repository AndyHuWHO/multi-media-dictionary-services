package com.word.mediaservice.media.controller;

import com.word.mediaservice.media.dto.GenerateUploadUrlResponseDTO;
import com.word.mediaservice.media.dto.MediaMetadataRequestDTO;
import com.word.mediaservice.media.dto.MediaMetadataResponseDTO;
import com.word.mediaservice.media.dto.MediaMetadataUpdateRequestDTO;
import com.word.mediaservice.media.service.MediaUploadService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
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
    public Mono<ResponseEntity<MediaMetadataResponseDTO>> saveMediaMetadata(
            @RequestHeader("X-Auth-UserId") String authUserId,
            @Valid @RequestBody MediaMetadataRequestDTO requestDTO
    ) {
        return mediaUploadService.saveMediaMetadata(authUserId, requestDTO)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/user")
    public Flux<MediaMetadataResponseDTO> getUserMedia(
            @RequestHeader("X-Auth-UserId") String authUserId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return mediaUploadService.getUserMedia(authUserId, page, size);
    }

    @GetMapping("/user/{userId}")
    public Flux<MediaMetadataResponseDTO> getPublicMediaByUser(
            @PathVariable String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return mediaUploadService.getPublicMediaByUser(userId, page, size);
    }

    @GetMapping("/word/{word}")
    public Flux<MediaMetadataResponseDTO> getPublicMediaByWord(
            @PathVariable String word,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return mediaUploadService.getPublicMediaByWord(word, page, size);
    }

    @GetMapping("/feed")
    public Flux<MediaMetadataResponseDTO> getFeed(
            @RequestParam(defaultValue = "0") int page) {
        return mediaUploadService.getFeed(page);
    }

    @PatchMapping("/{mediaId}")
    public Mono<ResponseEntity<MediaMetadataResponseDTO>> updateMediaMetadata(
            @RequestHeader("X-Auth-UserId") String authUserId,
            @PathVariable String mediaId,
            @Valid @RequestBody MediaMetadataUpdateRequestDTO dto) {
        return mediaUploadService.updateMediaMetadata(authUserId, mediaId, dto)
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{mediaId}")
    public Mono<ResponseEntity<Void>> deleteMedia(
            @RequestHeader("X-Auth-UserId") String authUserId,
            @PathVariable String mediaId) {
        return mediaUploadService.deleteMedia(authUserId, mediaId)
                .thenReturn(ResponseEntity.noContent().build());
    }


}
