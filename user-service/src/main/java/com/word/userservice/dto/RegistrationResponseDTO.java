package com.word.userservice.dto;

import com.word.userservice.model.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class RegistrationResponseDTO {
    private String publicId;
    private String email;
    private UserRole role;
    private LocalDateTime createdAt;
}
