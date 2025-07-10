package com.word.mediaservice.media.dto;

import com.word.mediaservice.media.model.Visibility;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@Builder
public class MediaMetadataResponseDTO {
    private String id;

    private String authUserId;

    private String objectPresignedGetUrl;
    private String thumbnailPresignedGetUrl;

    private Instant createdAt;
    private Instant updatedAt;

    private String description;
    private List<String> words;
    private List<String> tags;

    private int likeCount;
    private int commentCount;

    private double durationSeconds;
    private int fileSizeBytes;

    private Visibility visibility;
}
