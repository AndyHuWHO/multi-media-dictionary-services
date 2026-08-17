package com.word.mediaservice.media.dto;

import com.word.mediaservice.media.model.Visibility;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;

@Data
public class InitiateMediaUploadRequestDTO {
    @NotBlank(message = "Video content type is required.")
    private String videoContentType;

    @Size(max = 255, message = "Description must be less than 255 characters.")
    private String description;
    @NotEmpty(message = "At least one word is required.")
    @Size(max = 10, message = "You can associate up to 10 words.")
    private List<String> words;
    @Size(max = 5, message = "You can tag up to 5 items.")
    private List<String> tags;


    @Positive(message = "Duration must be greater than 0.")
    @DecimalMax(value = "30.0", message = "Maximum allowed video length is 30 seconds")
    private double durationSeconds;
    @Positive(message = "File size must be positive.")
    @Max(value = 20_000_000, message = "Maximum allowed file size is 20 MB.")
    private int fileSizeBytes;
    @NotNull(message = "Visibility must be specified.")
    private Visibility visibility;
}
