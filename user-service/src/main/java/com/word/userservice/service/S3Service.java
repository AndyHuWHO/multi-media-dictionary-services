package com.word.userservice.service;

import com.word.userservice.dto.PreSingedUrlResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

import java.time.Duration;

@Service
public class S3Service {
    @Value("${aws.region}")
    private String region;
    @Value("${aws.s3.bucket.name}")
    private String bucketName;
    public PreSingedUrlResponseDTO generatePresignedUploadUrl(String authUserId) {
        String objectKey = "profile-images/" + authUserId + ".jpg";
        Region awsRegion = Region.of(this.region);
        try (S3Presigner preSigner= S3Presigner.builder()
                .region(awsRegion)
                .credentialsProvider(DefaultCredentialsProvider.builder().build())
                .build()) {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .contentType("image/jpeg")
                    .build();
            PresignedPutObjectRequest presignedPutObjectRequest = preSigner.presignPutObject(r-> r
                            .signatureDuration(Duration.ofMinutes(3))
                            .putObjectRequest(putObjectRequest)
                    );
            String uploadUrl = presignedPutObjectRequest.url().toString();
            String publicUrl = String.format("https://%s.s3.%s.amazonaws.com/%s",
                    bucketName, region, objectKey);

            return PreSingedUrlResponseDTO.builder()
                    .uploadUrl(uploadUrl)
                    .publicUrl(publicUrl)
                    .build();

        }
    }
}
