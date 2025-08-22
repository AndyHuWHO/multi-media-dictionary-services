package com.word.mediaservice.engagement.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "media_like")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@CompoundIndex(name = "user_media_idx", def = "{'authUserId': 1, 'mediaId': 1}", unique = true)
@CompoundIndex(name = "media_idx", def = "{'mediaId': 1}")
@CompoundIndex(name = "authUser_idx", def = "{'authUserId': 1}")
public class MediaLike {
    @Id
    private String id;
    private String authUserId;
    private String mediaId;

    private Instant likedAt;
}
