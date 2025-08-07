package com.word.userservice.service;

import com.word.userservice.dto.UserProfileRequestDTO;
import com.word.userservice.dto.UserProfileResponseDTO;
import com.word.userservice.dto.VisitUserProfileResponseDTO;

public interface UserProfileService {
    UserProfileResponseDTO getProfileByAuthUserId(String authUserId);
    UserProfileResponseDTO updateProfile(String authUserId, UserProfileRequestDTO request);
    UserProfileResponseDTO createProfile(String authUserId);
    VisitUserProfileResponseDTO visitUserProfile(String authUserId);


}
