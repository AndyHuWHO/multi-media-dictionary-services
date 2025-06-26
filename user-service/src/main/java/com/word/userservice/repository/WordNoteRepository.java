package com.word.userservice.repository;

import com.word.userservice.model.WordNote;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WordNoteRepository extends JpaRepository<WordNote, Long> {
    // Fetch all notes in a notebook
    List<WordNote> findByNotebook_NotebookId(Long notebookId);

    // Check if a word already exists in a notebook
    Optional<WordNote> findByNotebook_NotebookIdAndWord(Long notebookId, String word);

    // Fetch paginated notes
    Page<WordNote> findByNotebook_NotebookId(Long notebookId, Pageable pageable);
}
