package com.word.authservice.dto;

import com.word.authservice.model.UserRole;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WebLoginResponseDTO {
    private String publicId;
    private String email;
    private UserRole role;
}
