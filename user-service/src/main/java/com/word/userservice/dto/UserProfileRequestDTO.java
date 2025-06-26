package com.word.userservice.dto;

import com.word.userservice.model.Gender;
import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.Size;

@Getter
@Setter
public class UserProfileRequestDTO {
    @Size(min = 1, max = 30)
    private String profileName;
    @Size(max = 300)
    private String bio;
    private String profileImageUrl;
    private Gender gender;
}
