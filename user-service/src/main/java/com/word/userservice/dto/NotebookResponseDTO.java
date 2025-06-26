package com.word.userservice.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
public class NotebookResponseDTO {
    private Long notebookId;
    private String title;
    private LocalDateTime dateCreated;
    private LocalDateTime dateUpdated;
}
