package com.word.userservice.service;

import com.word.userservice.dto.WordNoteRequestDTO;
import com.word.userservice.dto.WordNoteResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface WordNoteService {
    // Option A: Paginated
    Page<WordNoteResponseDTO> getNotesByNotebook(Long notebookId, String authUserId, Pageable pageable);

    // Option B: Or fetch all (non-paginated)
    List<WordNoteResponseDTO> getAllNotesByNotebook(Long notebookId, String authUserId);

    WordNoteResponseDTO createNote(Long notebookId, String authUserId, WordNoteRequestDTO request);

    WordNoteResponseDTO updateNote(Long noteId, String authUserId, WordNoteRequestDTO request);

    void deleteNote(Long noteId, String authUserId);
}
