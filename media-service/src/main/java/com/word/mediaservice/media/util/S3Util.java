package com.word.mediaservice.media.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

import java.time.Duration;

@Component
public class S3Util {
    private final S3Presigner preSigner;
    private final String bucketName;
    private final S3Client s3Client;

    public S3Util(@Value("${aws.s3.bucket.name}") String bucketName,
            @Value("${aws.region}") String region) {
        this.bucketName = bucketName;
        Region awsRegion = Region.of(region);
        this.preSigner = S3Presigner.builder()
                .region(awsRegion)
                .credentialsProvider(DefaultCredentialsProvider.builder().build())
                .build();
        this.s3Client = S3Client.builder()
                .region(awsRegion)
                .credentialsProvider(DefaultCredentialsProvider.create())
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

    public String generatePresignedGetUrl(String objectKey) {
        return preSigner.presignGetObject(presign ->
                presign.signatureDuration(Duration.ofMinutes(15))
                        .getObjectRequest(builder -> builder
                                .bucket(bucketName)
                                .key(objectKey)
                                .build())
        ).url().toString();
    }

    public Mono<Void> s3AsyncDelete(String objectKey) {
        return Mono.fromRunnable(() -> {
            DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .build();
            s3Client.deleteObject(deleteRequest);
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }
}
