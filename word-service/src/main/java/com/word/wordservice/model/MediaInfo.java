package com.word.wordservice.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@Document("mediaInfo")
public class MediaInfo {
    @Id
    private String id;

    @Indexed
    private String word;
    @Indexed
    private String uploadedBy;

    private String url;
    private String type;
    private Instant uploadedAt;
    private String sourceContext;
    private String languageSpoken;
    private List<String> tags;

    @Indexed
    private boolean isPublic;
}
