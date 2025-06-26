package com.word.userservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WordNoteRequestDTO {
    @NotBlank
    @Size(max = 100)
    private String word;

    @Size(max = 1000)
    private String content;
}
