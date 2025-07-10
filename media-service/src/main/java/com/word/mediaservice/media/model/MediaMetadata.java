package com.word.mediaservice.media.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Document(collection = "media_metadata")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@CompoundIndexes({
        // For: find all public media ordered by newest
        @CompoundIndex(name = "visibility_createdAt_idx", def = "{'visibility': 1, 'createdAt': -1}"),
        // For: find all public media associated with a specific word
        @CompoundIndex(name = "words_visibility_idx", def = "{'words': 1, 'visibility': 1}"),
        // For: top liked or commented public media
        @CompoundIndex(name = "likes_visibility_idx", def = "{'visibility': 1, 'likeCount': -1}"),
        @CompoundIndex(name = "comments_visibility_idx", def = "{'visibility': 1, 'commentCount': -1}"),
        @CompoundIndex(
                name = "user_visibility_createdAt_idx",
                def = "{'authUserId': 1, 'visibility': 1, 'createdAt': -1}"
        )
})
public class MediaMetadata {
    @Id
    private String id;
    @Indexed
    private String authUserId;
    // For: direct S3 lookup or deduplication
    @Indexed(unique = true)
    private String objectKey;
    @CreatedDate
    private Instant createdAt;
    @LastModifiedDate
    private Instant updatedAt;

    private String description;
    // For: filtering by word associations
    @Indexed
    private List<String> words;
    // For: filtering or recommending by tags
    @Indexed
    private List<String> tags;

    private int likeCount;
    private int commentCount;

    private double durationSeconds;
    private int fileSizeBytes;

    private String thumbnailKey;
    // For: public/private access filtering
    @Indexed
    private Visibility visibility;
}
