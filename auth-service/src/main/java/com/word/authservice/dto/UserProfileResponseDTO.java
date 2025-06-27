package com.word.authservice.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Data
@Getter
public class UserProfileResponseDTO {
    private String profileName;
    private String bio;
    private String profileImageUrl;
    private Gender gender;
    private LocalDateTime dateUpdated;
    private LocalDateTime dateCreated;

    private  enum Gender {
        MALE, FEMALE
    }

}
