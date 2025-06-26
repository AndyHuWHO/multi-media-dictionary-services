package com.word.userservice.service;

import com.word.userservice.dto.NotebookRequestDTO;
import com.word.userservice.dto.NotebookResponseDTO;

import java.util.List;

public interface NotebookService {

    List<NotebookResponseDTO> getNotebooksByAuthUserId(String authUserId);

    NotebookResponseDTO createNotebook(String authUserId, NotebookRequestDTO request);

    NotebookResponseDTO updateNotebook(Long notebookId, String authUserId, NotebookRequestDTO request);

    void deleteNotebook(Long notebookId, String authUserId);
}
