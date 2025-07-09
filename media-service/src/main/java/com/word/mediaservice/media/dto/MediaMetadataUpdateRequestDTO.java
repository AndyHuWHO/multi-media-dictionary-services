package com.word.mediaservice.media.dto;

import com.word.mediaservice.media.model.Visibility;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class MediaMetadataUpdateRequestDTO {
    @Size(max = 255, message = "Description must be less than 255 characters.")
    private String description;

    @Size(max = 5, message = "You can tag up to 5 items.")
    private List<String> tags;

    @Size(max = 10, message = "You can associate up to 10 words.")
    private List<String> words;

    private Visibility visibility;
}
