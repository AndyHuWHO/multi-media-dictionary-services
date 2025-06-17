package com.word.userservice.dto;

import com.word.userservice.model.UserRole;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class LoginResponseDTO {
    private String publicId;
    private String email;
    private UserRole role;
    private String token;
}
