package com.word.mediaservice.media.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

import java.time.Duration;

@Component
public class S3Util {
    private final S3Presigner preSigner;
    private final String bucketName;

    public S3Util(@Value("${aws.s3.bucket.name}") String bucketName,
            @Value("${aws.region}") String region) {
        this.bucketName = bucketName;
        this.preSigner = S3Presigner.builder()
                .region(Region.of(region))
                .credentialsProvider(DefaultCredentialsProvider.builder().build())
                .build();
    }

    // === Presigned PUT URLs ===
    public String generateVideoUploadUrl(String objectKey) {
        return generatePresignedPutUrl(objectKey, "video/mp4");
    }

    public String generateThumbnailUploadUrl(String objectKey) {
        return generatePresignedPutUrl(objectKey, "image/jpeg");
    }

    private String generatePresignedPutUrl(String objectKey, String contentType) {
        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .contentType(contentType)
                .build();

        PresignedPutObjectRequest presignedRequest = preSigner.presignPutObject(presign ->
                presign.signatureDuration(Duration.ofMinutes(15))
                        .putObjectRequest(objectRequest)
        );

        return presignedRequest.url().toString();
    }

    // === Object Key Builders ===
    public String buildVideoKey(String userId) {
        return buildKey(userId, "video", "mp4");
    }

    public String buildThumbnailKey(String userId) {
        return buildKey(userId, "thumbnail", "jpg");
    }

    private String buildKey(String userId, String type, String extension) {
        long timestamp = System.currentTimeMillis();
        return String.format("media/%s/%s_%d.%s", userId, type, timestamp, extension);
    }


}
