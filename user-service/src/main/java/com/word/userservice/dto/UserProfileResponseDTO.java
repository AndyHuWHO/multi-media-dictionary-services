package com.word.userservice.dto;

import com.word.userservice.model.Gender;
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

}
