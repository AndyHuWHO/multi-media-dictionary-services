package com.word.mediaservice.common.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserSnapshot {
    private String authUserId;
    private String profileImageUrl;
    private String profileName;
}
