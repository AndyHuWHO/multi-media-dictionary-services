package com.word.userservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotebookRequestDTO {
    @Size(max=100)
    @NotBlank
    private String title;
}
