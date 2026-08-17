package com.word.mediaservice.media.service;

import com.word.mediaservice.media.dto.MediaMetadataResponseDTO;
import com.word.mediaservice.media.dto.MediaMetadataUpdateRequestDTO;
import reactor.core.publisher.Mono;

public interface MediaManagementService {
    Mono<MediaMetadataResponseDTO> updateMediaMetadata(String authUserId,
                                                       String mediaId,
                                                       MediaMetadataUpdateRequestDTO dto);

    Mono<Void> deleteMedia(String authUserId, String mediaId);
}
