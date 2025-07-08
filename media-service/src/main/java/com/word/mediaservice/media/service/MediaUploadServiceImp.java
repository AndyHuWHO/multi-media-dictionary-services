package com.word.mediaservice.media.service;

import com.word.mediaservice.media.dto.GenerateUploadUrlResponseDTO;
import com.word.mediaservice.media.dto.MediaMetadataRequestDTO;
import com.word.mediaservice.media.dto.MediaMetadataResponseDTO;
import com.word.mediaservice.media.dto.MediaMetadataSaveResponseDTO;
import com.word.mediaservice.media.model.MediaMetadata;
import com.word.mediaservice.media.repository.MediaMetadataRepository;
import com.word.mediaservice.media.util.S3Util;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class MediaUploadServiceImp implements MediaUploadService{
    private final S3Util s3Util;
    private final MediaMetadataRepository mediaMetadataRepository;

    public MediaUploadServiceImp(S3Util s3Util, MediaMetadataRepository mediaMetadataRepository) {
        this.s3Util = s3Util;
        this.mediaMetadataRepository = mediaMetadataRepository;
    }

    @Override
    public Mono<Void> validateMediaMetadata(MediaMetadataRequestDTO dto) {
        return mediaMetadataRepository.findByObjectKey(dto.getObjectKey())
                .flatMap(existing -> Mono.error(new IllegalArgumentException("Duplicate objectKey: already used. Please request a new upload URL.")))
                .then();
    }

    @Override
    public Mono<GenerateUploadUrlResponseDTO> generatePresignedUrls(String authUserId) {
        String videoKey = s3Util.buildVideoKey(authUserId);
        String thumbnailKey = s3Util.buildThumbnailKey(authUserId);

        String videoUploadUrl = s3Util.generateVideoUploadUrl(videoKey);
        String thumbnailUploadUrl = s3Util.generateThumbnailUploadUrl(thumbnailKey);

        GenerateUploadUrlResponseDTO response = GenerateUploadUrlResponseDTO.builder()
                .uploadUrl(videoUploadUrl)
                .objectKey(videoKey)
                .thumbnailUploadUrl(thumbnailUploadUrl)
                .thumbnailKey(thumbnailKey)
                .build();
        return Mono.just(response);
    }

    @Override
    public Mono<MediaMetadataSaveResponseDTO> saveMediaMetadata(String authUserId, MediaMetadataRequestDTO dto) {
        MediaMetadata metadata = MediaMetadata.builder()
                .authUserId(authUserId)
                .objectKey(dto.getObjectKey())
                .thumbnailKey(dto.getThumbnailKey())
                .description(dto.getDescription())
                .words(dto.getWords())
                .tags(dto.getTags())
                .durationSeconds(dto.getDurationSeconds())
                .fileSizeBytes(dto.getFileSizeBytes())
                .visibility(dto.getVisibility())
                .likeCount(0)
                .commentCount(0)
                .build();

        return mediaMetadataRepository.save(metadata)
                .map(this::toSaveResponseDTO);
    }

    private MediaMetadataSaveResponseDTO toSaveResponseDTO(MediaMetadata media) {
        return MediaMetadataSaveResponseDTO.builder()
                .id(media.getId())
                .authUserId(media.getAuthUserId())
                .objectKey(media.getObjectKey())
                .thumbnailKey(media.getThumbnailKey())
                .description(media.getDescription())
                .words(media.getWords())
                .tags(media.getTags())
                .durationSeconds(media.getDurationSeconds())
                .fileSizeBytes(media.getFileSizeBytes())
                .visibility(media.getVisibility())
                .createdAt(media.getCreatedAt())
                .updatedAt(media.getUpdatedAt())
                .build();
    }

}
