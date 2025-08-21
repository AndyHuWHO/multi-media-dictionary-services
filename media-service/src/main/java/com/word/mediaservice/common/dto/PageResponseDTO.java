package com.word.mediaservice.common.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PageResponseDTO<T> {
    private List<T> content;
    private int page;
    private int pageSize;
    private int totalPages;
}
