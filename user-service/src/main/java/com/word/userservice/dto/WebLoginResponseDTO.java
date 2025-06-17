package com.word.userservice.dto;

import com.word.userservice.model.UserRole;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WebLoginResponseDTO {
    private String publicId;
    private String email;
    private UserRole role;
}
