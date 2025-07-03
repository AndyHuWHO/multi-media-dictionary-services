package com.word.userservice.controller;

import com.word.userservice.dto.PreSingedUrlResponseDTO;
import com.word.userservice.dto.UserProfileRequestDTO;
import com.word.userservice.dto.UserProfileResponseDTO;
import com.word.userservice.service.S3Service;
import com.word.userservice.service.UserProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user/profile")
@Tag(name = "User Profile", description = "User Profile API")
public class UserProfileController {
    private final UserProfileService userProfileService;
    private final S3Service s3Service;

    public UserProfileController(UserProfileService userProfileService, S3Service s3Service) {
        this.userProfileService = userProfileService;
        this.s3Service = s3Service;
    }

    @GetMapping
    @Operation(summary = "Get user profile")
    public ResponseEntity<UserProfileResponseDTO> getProfile (
            @RequestHeader("X-Auth-UserId") String authUserId) {
        return ResponseEntity.ok(userProfileService.getProfileByAuthUserId(authUserId));
    }

    @PatchMapping
    @Operation(summary = "Update user profile")
    public ResponseEntity<UserProfileResponseDTO> updateProfile(
            @RequestHeader("X-Auth-UserId") String authUserId,
            @Valid @RequestBody UserProfileRequestDTO request) {
        return ResponseEntity.ok(userProfileService.updateProfile(authUserId, request));
    }

    @PostMapping
    public ResponseEntity<UserProfileResponseDTO> createProfile(
            @RequestHeader("X-Auth-UserId") String authUserId) {
        return ResponseEntity.ok(userProfileService.createProfile(authUserId));
    }

    @PostMapping("/profile-image/upload-url")
    @Operation(summary = "Get pre-signed upload URL for profile image")
    public ResponseEntity<PreSingedUrlResponseDTO> getProfileImageUploadUrl(
            @RequestHeader("X-Auth-UserId") String authUserId) {

        PreSingedUrlResponseDTO uploadInfo = s3Service.generatePresignedUploadUrl(authUserId);

        return ResponseEntity.ok(uploadInfo);
    }
}
