package com.word.mediaservice.media.model;

import com.word.mediaservice.common.model.UserSnapshot;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "media_metadata")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MediaMetadata {
    @Id
    private String id;

    private UserSnapshot uploader;

    private String objectKey;
    private LocalDateTime dateCreated;
    private LocalDateTime dateUpdated;

    private double durationSeconds;

}
