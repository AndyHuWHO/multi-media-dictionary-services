package com.word.userservice.dto;

import com.word.userservice.model.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VisitUserProfileResponseDTO {
    private String authUserId;
    private String profileName;
    private String bio;
    private Gender gender;
    private String profileImageUrl;

}
