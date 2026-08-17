package com.word.mediaservice.media.service;

import com.word.mediaservice.engagement.repository.MediaLikeRepository;
import com.word.mediaservice.media.dto.MediaMetadataResponseDTO;
import com.word.mediaservice.media.dto.MediaMetadataUpdateRequestDTO;
import com.word.mediaservice.media.repository.MediaMetadataRepository;
import com.word.mediaservice.media.util.MediaMetadataMapper;
import com.word.mediaservice.media.util.S3Util;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class MediaManagementServiceImpl implements MediaManagementService {
    private final S3Util s3Util;
    private final MediaMetadataRepository mediaMetadataRepository;
    private final MediaMetadataMapper mediaMetadataMapper;
    private final MediaLikeRepository mediaLikeRepository;

    public MediaManagementServiceImpl(S3Util s3Util,
                                      MediaMetadataRepository mediaMetadataRepository,
                                      MediaMetadataMapper mediaMetadataMapper,
                                      MediaLikeRepository mediaLikeRepository) {
        this.s3Util = s3Util;
        this.mediaMetadataRepository = mediaMetadataRepository;
        this.mediaMetadataMapper = mediaMetadataMapper;
        this.mediaLikeRepository = mediaLikeRepository;
    }

    @Override
    public Mono<MediaMetadataResponseDTO> updateMediaMetadata(String authUserId,
                                                              String mediaId,
                                                              MediaMetadataUpdateRequestDTO dto) {
        return mediaMetadataRepository.findById(mediaId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Media not found.")))
                .flatMap(media -> {
                    if (!media.getAuthUserId().equals(authUserId)) {
                        return Mono.error(new IllegalArgumentException("Update failed. " +
                                "You are not the owner of this media."));
                    }
                    if (dto.getDescription() != null) {
                        media.setDescription(dto.getDescription());
                    }
                    if (dto.getTags() != null) {
                        media.setTags(dto.getTags());
                    }
                    if (dto.getWords() != null) {
                        media.setWords(dto.getWords());
                    }
                    if (dto.getVisibility() != null) {
                        media.setVisibility(dto.getVisibility());
                    }

                    return mediaMetadataRepository.save(media)
                            .map(mediaMetadataMapper::toResponseDTO);
                });
    }

    @Override
    public Mono<Void> deleteMedia(String authUserId, String mediaId) {
        return mediaMetadataRepository.findById(mediaId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Media not found.")))
                .flatMap(media -> {
                    if (!media.getAuthUserId().equals(authUserId)) {
                        return Mono.error(new IllegalArgumentException("Delete failed. " +
                                "You are not the owner of this media."));
                    }

                    String videoKey = media.getObjectKey();
                    String thumbnailKey = media.getThumbnailKey();

//                    return s3Util.s3AsyncDelete(videoKey)
//                            .then(s3Util.s3AsyncDelete(thumbnailKey))
//                            .then(mediaMetadataRepository.delete(media));
                    // Step 1: Delete all likes for this media
                    return mediaLikeRepository.deleteAllByMediaId(mediaId)
                            // Step 2: Delete media metadata
                            .then(mediaMetadataRepository.delete(media))
                            // Step 3: Delete media from S3
                            .then(s3Util.s3AsyncDelete(videoKey))
                            // Step 4: Delete thumbnail from S3
                            .then(s3Util.s3AsyncDelete(thumbnailKey));

                });
    }
}
