package com.word.userservice.controller;

import com.word.userservice.dto.WordNoteRequestDTO;
import com.word.userservice.dto.WordNoteResponseDTO;
import com.word.userservice.service.WordNoteService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user/notebooks/{notebookId}/notes")
public class WordNoteController {
    private final WordNoteService wordNoteService;

    public WordNoteController(WordNoteService wordNoteService) {
        this.wordNoteService = wordNoteService;
    }

    @GetMapping
    public ResponseEntity<Page<WordNoteResponseDTO>> getNotes(
            @PathVariable Long notebookId,
            @RequestHeader("X-Auth-UserId") String authUserId,
            Pageable pageable) {
        return ResponseEntity.ok(wordNoteService.getNotesByNotebook(notebookId, authUserId, pageable));
    }

    @GetMapping("/all")
    public ResponseEntity<List<WordNoteResponseDTO>> getAllNotes(
            @PathVariable Long notebookId,
            @RequestHeader("X-Auth-UserId") String authUserId) {
        return ResponseEntity.ok(wordNoteService.getAllNotesByNotebook(notebookId, authUserId));
    }

    @PostMapping
    public ResponseEntity<WordNoteResponseDTO> createNote(
            @PathVariable Long notebookId,
            @RequestHeader("X-Auth-UserId") String authUserId,
            @Valid @RequestBody WordNoteRequestDTO request) {
        return ResponseEntity.ok(wordNoteService.createNote(notebookId, authUserId, request));
    }

    @PatchMapping("/{noteId}")
    public ResponseEntity<WordNoteResponseDTO> updateNote(
            @PathVariable Long noteId,
            @RequestHeader("X-Auth-UserId") String authUserId,
            @Valid @RequestBody WordNoteRequestDTO request) {
        return ResponseEntity.ok(wordNoteService.updateNote(noteId, authUserId, request));
    }

    @DeleteMapping("/{noteId}")
    public ResponseEntity<Void> deleteNote(
            @PathVariable Long noteId,
            @RequestHeader("X-Auth-UserId") String authUserId) {
        wordNoteService.deleteNote(noteId, authUserId);
        return ResponseEntity.noContent().build();
    }
}
