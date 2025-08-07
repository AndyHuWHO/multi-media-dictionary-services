package com.word.userservice.service;

import com.word.userservice.dto.UserProfileRequestDTO;
import com.word.userservice.dto.UserProfileResponseDTO;
import com.word.userservice.dto.VisitUserProfileResponseDTO;
import com.word.userservice.exception.UserProfileNotFoundException;
import com.word.userservice.model.UserProfile;
import com.word.userservice.repository.UserProfileRepository;
import org.springframework.stereotype.Service;


@Service
public class UserProfileServiceImpl implements UserProfileService{
    private final UserProfileRepository userProfileRepository;

    public UserProfileServiceImpl(UserProfileRepository userProfileRepository) {
        this.userProfileRepository = userProfileRepository;
    }

    @Override
    public UserProfileResponseDTO getProfileByAuthUserId(String authUserId) {
        UserProfile userProfile = userProfileRepository.findByAuthUserId(authUserId)
                .orElseThrow(() -> new UserProfileNotFoundException("User profile not found"));

        return mapToResponseDto(userProfile);
    }

    @Override
    public UserProfileResponseDTO updateProfile(String authUserId, UserProfileRequestDTO request) {
        UserProfile userProfile = userProfileRepository.findByAuthUserId(authUserId)
                .orElseThrow(() -> new UserProfileNotFoundException("Couldn't find User profile for update"));
        userProfile.setProfileName(request.getProfileName());
        userProfile.setBio(request.getBio());
        userProfile.setProfileImageUrl(request.getProfileImageUrl());
        userProfile.setGender(request.getGender());
        UserProfile updated = userProfileRepository.save(userProfile);


        return mapToResponseDto(updated);
    }


    private UserProfileResponseDTO mapToResponseDto(UserProfile profile) {
        return UserProfileResponseDTO.builder()
                .profileName(profile.getProfileName())
                .publicId(profile.getAuthUserId())
                .bio(profile.getBio())
                .profileImageUrl(profile.getProfileImageUrl())
                .gender(profile.getGender())
                .dateUpdated(profile.getDateUpdated())
                .dateCreated(profile.getDateCreated())
                .build();
    }

    @Override
    public UserProfileResponseDTO createProfile(String authUserId) {
        if (authUserId == null || authUserId.trim().isEmpty()) {
            throw new IllegalArgumentException("authUserId must not be null or blank");
        }

        UserProfile profile = UserProfile.builder()
                .authUserId(authUserId)
                .profileName(authUserId.substring(0,10)) // Temp name to satisfy unique constraint
                .build();

        return mapToResponseDto(userProfileRepository.save(profile));
    }

    @Override
    public VisitUserProfileResponseDTO visitUserProfile(String authUserId) {
        UserProfile profile = userProfileRepository.findByAuthUserId(authUserId)
                .orElseThrow(() -> new UserProfileNotFoundException("User profile not found"));

        return VisitUserProfileResponseDTO.builder()
                .profileName(profile.getProfileName())
                .authUserId(profile.getAuthUserId())
                .bio(profile.getBio())
                .profileImageUrl(profile.getProfileImageUrl())
                .gender(profile.getGender())
                .build();
    }

}
