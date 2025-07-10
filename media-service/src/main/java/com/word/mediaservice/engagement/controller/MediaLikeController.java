package com.word.mediaservice.engagement.controller;

import com.word.mediaservice.engagement.service.MediaLikeService;
import com.word.mediaservice.media.dto.MediaMetadataResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/media")
public class MediaLikeController {
    private final MediaLikeService mediaLikeService;

    public MediaLikeController(MediaLikeService mediaLikeService) {
        this.mediaLikeService = mediaLikeService;
    }

    @PostMapping("/{mediaId}/like")
    public Mono<ResponseEntity<Void>> likeMedia(@RequestHeader("X-Auth-UserId") String authUserId,
                                          @PathVariable("mediaId") String mediaId) {
        return mediaLikeService.likeMedia(authUserId, mediaId)
                .thenReturn(ResponseEntity.ok().build());

    }

    @DeleteMapping("/{mediaId}/like")
    public Mono<ResponseEntity<Void>> unlikeMedia(
            @RequestHeader("X-Auth-UserId") String authUserId,
            @PathVariable("mediaId") String mediaId) {
        return mediaLikeService.unlikeMedia(authUserId, mediaId)
                .thenReturn(ResponseEntity.noContent().build());
    }

    @GetMapping("/likes/user")
    public Flux<MediaMetadataResponseDTO> getLikedMedia(
            @RequestHeader("X-Auth-UserId") String authUserId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return mediaLikeService.getLikedMedia(authUserId, page, size);
    }

    @GetMapping("/likes/status")
    public Mono<List<String>> getLikedMediaIds(
            @RequestHeader("X-Auth-UserId") String authUserId,
            @RequestParam("mediaIds") List<String> mediaIds
    ) {
        return mediaLikeService.getLikedMediaIds(authUserId, mediaIds);
    }

}
