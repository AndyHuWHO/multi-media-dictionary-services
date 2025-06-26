package com.word.userservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "word_note",
        uniqueConstraints =
                {@UniqueConstraint(columnNames = {"notebook_id", "word"})
})
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WordNote {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long noteId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notebook_id", nullable = false)
    private Notebook notebook;

    @Column(nullable = false, length = 100)
    private String word;

    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDateTime dateCreated;
    private LocalDateTime dateUpdated;

    @PrePersist
    protected void onCreate() {
        this.dateCreated = this.dateUpdated = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.dateUpdated = LocalDateTime.now();
    }
}
