package com.word.mediaservice.media.util;

import com.word.mediaservice.media.dto.MediaMetadataResponseDTO;
import com.word.mediaservice.media.model.MediaMetadata;
import org.springframework.stereotype.Component;

@Component
public class MediaMetadataMapper {
    private final S3Util s3Util;

    public MediaMetadataMapper(S3Util s3Util) {
        this.s3Util = s3Util;
    }

    public MediaMetadataResponseDTO toResponseDTO(MediaMetadata metadata) {
        return MediaMetadataResponseDTO.builder()
                .id(metadata.getId())
                .authUserId(metadata.getAuthUserId())
                .objectPresignedGetUrl(s3Util.generatePresignedGetUrl(metadata.getObjectKey()))
                .thumbnailPresignedGetUrl(s3Util.generatePresignedGetUrl(metadata.getThumbnailKey()))
                .createdAt(metadata.getCreatedAt())
                .updatedAt(metadata.getUpdatedAt())
                .description(metadata.getDescription())
                .words(metadata.getWords())
                .tags(metadata.getTags())
                .likeCount(metadata.getLikeCount())
                .commentCount(metadata.getCommentCount())
                .durationSeconds(metadata.getDurationSeconds())
                .fileSizeBytes(metadata.getFileSizeBytes())
                .visibility(metadata.getVisibility())
                .build();
    }
}
