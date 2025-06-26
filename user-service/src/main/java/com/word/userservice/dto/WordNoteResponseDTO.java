package com.word.userservice.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
@Data
@Builder
public class WordNoteResponseDTO {
    private Long noteId;
    private String word;
    private String content;
    private LocalDateTime dateCreated;
    private LocalDateTime dateUpdated;
}
