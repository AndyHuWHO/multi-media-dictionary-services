package com.word.userservice.controller;

import com.word.userservice.dto.NotebookRequestDTO;
import com.word.userservice.dto.NotebookResponseDTO;
import com.word.userservice.service.NotebookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user/notebooks")
@Tag(name = "Notebooks", description = "Notebooks API")
public class NotebookController {
    private final NotebookService notebookService;

    public NotebookController(NotebookService notebookService) {
        this.notebookService = notebookService;
    }

    @GetMapping
    @Operation(summary = "Get user notebooks")
    public ResponseEntity<List<NotebookResponseDTO>> getUserNotebooks(
            @RequestHeader("X-Auth-UserId") String authUserId) {
        return ResponseEntity.ok(notebookService.getNotebooksByAuthUserId(authUserId));
    }

    @PostMapping
    @Operation(summary = "Create a new notebook")
    public ResponseEntity<NotebookResponseDTO> createNotebook(
            @RequestHeader("X-Auth-UserId") String authUserId,
            @Valid @RequestBody NotebookRequestDTO request) {
        return ResponseEntity.ok(notebookService.createNotebook(authUserId, request));
    }

    @PatchMapping("/{notebookId}")
    @Operation(summary = "Update a notebook")
    public ResponseEntity<NotebookResponseDTO> updateNotebook(
            @PathVariable Long notebookId,
            @RequestHeader("X-Auth-UserId") String authUserId,
            @Valid @RequestBody NotebookRequestDTO request) {
        return ResponseEntity.ok(notebookService.updateNotebook(notebookId, authUserId, request));
    }

    @DeleteMapping("/{notebookId}")
    @Operation(summary = "Delete a notebook")
    public ResponseEntity<Void> deleteNotebook(
            @PathVariable Long notebookId,
            @RequestHeader("X-Auth-UserId") String authUserId) {
        notebookService.deleteNotebook(notebookId, authUserId);
        return ResponseEntity.noContent().build();
    }
}
