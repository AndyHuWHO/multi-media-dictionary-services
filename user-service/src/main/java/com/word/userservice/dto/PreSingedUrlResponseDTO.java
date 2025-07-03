package com.word.userservice.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class PreSingedUrlResponseDTO {
    private String uploadUrl;
    private String publicUrl;
}
