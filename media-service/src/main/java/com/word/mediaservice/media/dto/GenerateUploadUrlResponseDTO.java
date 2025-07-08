package com.word.mediaservice.media.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GenerateUploadUrlResponseDTO {
    private String uploadUrl;           // Presigned PUT URL to upload video (private)
    private String objectKey;           // S3 object key for the video

    private String thumbnailUploadUrl;  // Presigned PUT URL to upload thumbnail (public)
    private String thumbnailKey;        // S3 object key for the thumbnail
}
