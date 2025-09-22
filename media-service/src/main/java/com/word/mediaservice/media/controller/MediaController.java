package com.word.mediaservice.media.controller;

import com.word.mediaservice.common.dto.PageResponseDTO;
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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/media")
@Tag(name = "Media", description = "Media API")
public class MediaController {
    private final MediaUploadService mediaUploadService;

    public MediaController(MediaUploadService mediaUploadService) {
        this.mediaUploadService = mediaUploadService;
    }

    @PostMapping("/validate")
    @Operation(summary = "Validate metadata before uploading")
    public Mono<ResponseEntity<Void>> validateMetadata(
            @Valid @RequestBody MediaMetadataRequestDTO requestDTO
    ) {
        // If validation passes, just return 204 No Content
        return mediaUploadService.validateMediaMetadata(requestDTO)
                .thenReturn(ResponseEntity.noContent().build());
    }

    @PostMapping("/upload-url")
    @Operation(summary = "Generate upload URLs for media files")
    public Mono<ResponseEntity<GenerateUploadUrlResponseDTO>> generateUploadUrls(
            @RequestHeader("X-Auth-UserId") String userId,
            @RequestHeader("Content-Type") String contentType
    ) {
        return mediaUploadService.generatePresignedUrls(userId, contentType)
                .map(ResponseEntity::ok);
    }

    @PostMapping
    @Operation(summary = "Upload media-metadata")
    public Mono<ResponseEntity<MediaMetadataResponseDTO>> saveMediaMetadata(
            @RequestHeader("X-Auth-UserId") String authUserId,
            @Valid @RequestBody MediaMetadataRequestDTO requestDTO
    ) {
        return mediaUploadService.saveMediaMetadata(authUserId, requestDTO)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/user")
    @Operation(summary = "Get the current user's media")
    public Flux<MediaMetadataResponseDTO> getUserMedia(
            @RequestHeader("X-Auth-UserId") String authUserId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return mediaUploadService.getUserMedia(authUserId, page, size);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get a specified user's public media")
    public Flux<MediaMetadataResponseDTO> getPublicMediaByUser(
            @PathVariable String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return mediaUploadService.getPublicMediaByUser(userId, page, size);
    }

//    @GetMapping("/word/{word}")
//    @Operation(summary = "Get media by word")
//    public Flux<MediaMetadataResponseDTO> getPublicMediaByWord(
//            @PathVariable String word,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size
//    ) {
//        return mediaUploadService.getPublicMediaByWord(word, page, size);
//    }
//
//    @GetMapping("/feed")
//    @Operation(summary = "Get the latest and most interacted media feed")
//    public Flux<MediaMetadataResponseDTO> getFeed(
//            @RequestParam(defaultValue = "0") int page) {
//        return mediaUploadService.getFeed(page);
//    }

    // ...imports and annotations remain unchanged

    @GetMapping("/word/{word}")
    @Operation(summary = "Get media by word (paged)")
    public Mono<ResponseEntity<PageResponseDTO<MediaMetadataResponseDTO>>> getPublicMediaByWordPaged(
            @PathVariable String word,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return mediaUploadService.getPublicMediaByWordPaged(word, page, size)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/feed")
    @Operation(summary = "Get the latest and most interacted media feed (paged)")
    public Mono<ResponseEntity<PageResponseDTO<MediaMetadataResponseDTO>>> getFeedPaged(
            @RequestParam(defaultValue = "0") int page
    ) {
        return mediaUploadService.getFeedPaged(page)
                .map(ResponseEntity::ok);
    }




    @PatchMapping("/{mediaId}")
    @Operation(summary = "Update media metadata")
    public Mono<ResponseEntity<MediaMetadataResponseDTO>> updateMediaMetadata(
            @RequestHeader("X-Auth-UserId") String authUserId,
            @PathVariable String mediaId,
            @Valid @RequestBody MediaMetadataUpdateRequestDTO dto) {
        return mediaUploadService.updateMediaMetadata(authUserId, mediaId, dto)
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{mediaId}")
    @Operation(summary = "Delete media")
    public Mono<ResponseEntity<Void>> deleteMedia(
            @RequestHeader("X-Auth-UserId") String authUserId,
            @PathVariable String mediaId) {
        return mediaUploadService.deleteMedia(authUserId, mediaId)
                .thenReturn(ResponseEntity.noContent().build());
    }
}
