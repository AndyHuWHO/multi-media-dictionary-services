package com.word.mediaservice.media.dto;

import com.word.mediaservice.media.model.Visibility;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;
@Data
@Builder
public class MediaMetadataSaveResponseDTO {
    private String id;
    private String authUserId;

    private String objectKey;
    private String thumbnailKey;

    private String description;
    private List<String> words;
    private List<String> tags;

    private double durationSeconds;
    private int fileSizeBytes;

    private Visibility visibility;

    private Instant createdAt;
    private Instant updatedAt;
}
