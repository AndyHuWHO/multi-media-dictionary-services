package com.word.userservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "notebook")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Notebook {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long notebookId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    private UserProfile userProfile;

    @Column(nullable = false, length = 100)
    private String title;

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
