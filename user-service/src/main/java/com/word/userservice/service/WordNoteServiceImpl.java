package com.word.userservice.service;

import com.word.userservice.dto.WordNoteRequestDTO;
import com.word.userservice.dto.WordNoteResponseDTO;
import com.word.userservice.exception.NotebookNotFoundException;
import com.word.userservice.exception.UnauthorizedNotebookAccessException;
import com.word.userservice.exception.WordNoteNotFoundException;
import com.word.userservice.model.Notebook;
import com.word.userservice.model.WordNote;
import com.word.userservice.repository.NotebookRepository;
import com.word.userservice.repository.WordNoteRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WordNoteServiceImpl implements WordNoteService{
    private final WordNoteRepository wordNoteRepository;
    private final NotebookRepository notebookRepository;

    public WordNoteServiceImpl(WordNoteRepository wordNoteRepository, NotebookRepository notebookRepository) {
        this.wordNoteRepository = wordNoteRepository;
        this.notebookRepository = notebookRepository;
    }

    @Override
    public Page<WordNoteResponseDTO> getNotesByNotebook(Long notebookId, String authUserId, Pageable pageable) {
        getNotebookIfOwnedBy(notebookId, authUserId);
        return wordNoteRepository.findByNotebook_NotebookId(notebookId, pageable)
                .map(this::mapToDto);
    }

    @Override
    public List<WordNoteResponseDTO> getAllNotesByNotebook(Long notebookId, String authUserId) {
        getNotebookIfOwnedBy(notebookId, authUserId);
        return wordNoteRepository.findByNotebook_NotebookId(notebookId)
                .stream().map(this::mapToDto).toList();
    }

    @Override
    public WordNoteResponseDTO createNote(Long notebookId, String authUserId, WordNoteRequestDTO request) {
        Notebook notebook = getNotebookIfOwnedBy(notebookId, authUserId);
        WordNote note = WordNote.builder()
                .word(request.getWord())
                .content(request.getContent())
                .notebook(notebook)
                .build();
        return mapToDto(wordNoteRepository.save(note));
    }

    @Override
    public WordNoteResponseDTO updateNote(Long noteId, String authUserId, WordNoteRequestDTO request) {
        WordNote note = wordNoteRepository.findById(noteId)
                .orElseThrow(() -> new WordNoteNotFoundException("Note not found"));
        if (!note.getNotebook().getUserProfile().getAuthUserId().equals(authUserId)) {
            throw new UnauthorizedNotebookAccessException("Unauthorized access to note");
        }
        note.setContent(request.getContent());
        note.setWord(request.getWord());
        return mapToDto(wordNoteRepository.save(note));
    }

    @Override
    public void deleteNote(Long noteId, String authUserId) {
        WordNote note = wordNoteRepository.findById(noteId)
                .orElseThrow(() -> new WordNoteNotFoundException("Note not found"));
        if (!note.getNotebook().getUserProfile().getAuthUserId().equals(authUserId)) {
            throw new UnauthorizedNotebookAccessException("Unauthorized access to note");
        }
        wordNoteRepository.delete(note);
    }

    private Notebook getNotebookIfOwnedBy(Long notebookId, String authUserId) {
        Notebook notebook = notebookRepository.findById(notebookId)
                .orElseThrow(() -> new NotebookNotFoundException("Notebook not found"));

        if (!notebook.getUserProfile().getAuthUserId().equals(authUserId)) {
            throw new UnauthorizedNotebookAccessException("Notebook not owned by user");
        }
        return notebook;
    }


    private WordNoteResponseDTO mapToDto(WordNote note) {
        return WordNoteResponseDTO.builder()
                .noteId(note.getNoteId())
                .word(note.getWord())
                .content(note.getContent())
                .dateCreated(note.getDateCreated())
                .dateUpdated(note.getDateUpdated())
                .build();
    }
}
