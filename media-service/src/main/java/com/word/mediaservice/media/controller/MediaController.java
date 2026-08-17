package com.word.mediaservice.media.controller;

import com.word.mediaservice.common.dto.PageResponseDTO;
import com.word.mediaservice.media.dto.*;
import com.word.mediaservice.media.service.MediaManagementService;
import com.word.mediaservice.media.service.MediaQueryService;
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
    private final MediaQueryService mediaQueryService;
    private final MediaManagementService mediaManagementService;

    public MediaController(MediaUploadService mediaUploadService,
                           MediaQueryService mediaQueryService,
                           MediaManagementService mediaManagementService) {
        this.mediaUploadService = mediaUploadService;
        this.mediaQueryService = mediaQueryService;
        this.mediaManagementService = mediaManagementService;
    }


    @PostMapping("/upload/initiate")
    @Operation(summary = "Initiate media upload, validate metadata and get upload urls")
    public Mono<ResponseEntity<GenerateUploadUrlResponseDTO>> initiateMediaUpload(
            @RequestHeader("X-Auth-UserId") String authUserId,
            @Valid @RequestBody InitiateMediaUploadRequestDTO request
    ) {
        return mediaUploadService.generatePresignedUrls(
                authUserId,
                request.getVideoContentType()
        ).map(ResponseEntity::ok);
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
        return mediaQueryService.getUserMedia(authUserId, page, size);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get a specified user's public media")
    public Flux<MediaMetadataResponseDTO> getPublicMediaByUser(
            @PathVariable String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return mediaQueryService.getPublicMediaByUser(userId, page, size);
    }

    @GetMapping("/word/{word}")
    @Operation(summary = "Get media by word (paged)")
    public Mono<ResponseEntity<PageResponseDTO<MediaMetadataResponseDTO>>> getPublicMediaByWordPaged(
            @PathVariable String word,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return mediaQueryService.getPublicMediaByWordPaged(word, page, size)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/feed")
    @Operation(summary = "Get the latest and most interacted media feed (paged)")
    public Mono<ResponseEntity<PageResponseDTO<MediaMetadataResponseDTO>>> getFeedPaged(
            @RequestParam(defaultValue = "0") int page
    ) {
        return mediaQueryService.getFeedPaged(page)
                .map(ResponseEntity::ok);
    }



    @PatchMapping("/{mediaId}")
    @Operation(summary = "Update media metadata")
    public Mono<ResponseEntity<MediaMetadataResponseDTO>> updateMediaMetadata(
            @RequestHeader("X-Auth-UserId") String authUserId,
            @PathVariable String mediaId,
            @Valid @RequestBody MediaMetadataUpdateRequestDTO dto) {
        return mediaManagementService.updateMediaMetadata(authUserId, mediaId, dto)
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{mediaId}")
    @Operation(summary = "Delete media")
    public Mono<ResponseEntity<Void>> deleteMedia(
            @RequestHeader("X-Auth-UserId") String authUserId,
            @PathVariable String mediaId) {
        return mediaManagementService.deleteMedia(authUserId, mediaId)
                .thenReturn(ResponseEntity.noContent().build());
    }
}
