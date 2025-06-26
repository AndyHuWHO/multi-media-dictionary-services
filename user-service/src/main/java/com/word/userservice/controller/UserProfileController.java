package com.word.userservice.controller;

import com.word.userservice.dto.UserProfileRequestDTO;
import com.word.userservice.dto.UserProfileResponseDTO;
import com.word.userservice.service.UserProfileService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user/profile")
public class UserProfileController {
    private final UserProfileService userProfileService;

    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @GetMapping
    public ResponseEntity<UserProfileResponseDTO> getProfile (
            @RequestHeader("X-Auth-UserId") String authUserId) {
        return ResponseEntity.ok(userProfileService.getProfileByAuthUserId(authUserId));
    }

    @PatchMapping
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
}
