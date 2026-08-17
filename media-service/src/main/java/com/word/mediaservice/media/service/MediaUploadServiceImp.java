package com.word.mediaservice.media.service;

import com.word.mediaservice.media.dto.GenerateUploadUrlResponseDTO;
import com.word.mediaservice.media.dto.MediaMetadataRequestDTO;
import com.word.mediaservice.media.dto.MediaMetadataResponseDTO;
import com.word.mediaservice.media.model.MediaMetadata;
import com.word.mediaservice.media.repository.MediaMetadataRepository;
import com.word.mediaservice.media.util.MediaMetadataMapper;
import com.word.mediaservice.media.util.S3Util;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class MediaUploadServiceImp implements MediaUploadService {
    private final S3Util s3Util;
    private final MediaMetadataRepository mediaMetadataRepository;
    private final MediaMetadataMapper mediaMetadataMapper;

    public MediaUploadServiceImp(S3Util s3Util, MediaMetadataRepository mediaMetadataRepository, MediaMetadataMapper mediaMetadataMapper) {
        this.s3Util = s3Util;
        this.mediaMetadataRepository = mediaMetadataRepository;
        this.mediaMetadataMapper = mediaMetadataMapper;
    }


    @Override
    public Mono<GenerateUploadUrlResponseDTO> generatePresignedUrls(String authUserId, String contentType) {
        String videoKey = s3Util.buildVideoKey(authUserId);
        String thumbnailKey = s3Util.buildThumbnailKey(authUserId);

        String videoUploadUrl = s3Util.generateVideoUploadUrl(videoKey,contentType);
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
    public Mono<MediaMetadataResponseDTO> saveMediaMetadata(String authUserId, MediaMetadataRequestDTO dto) {
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
                .map(mediaMetadataMapper::toResponseDTO);
    }


}
