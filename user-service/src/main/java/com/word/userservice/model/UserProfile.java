package com.word.userservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_profile")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long profileId;

    @Column(nullable = false, unique = true)
    private String authUserId;

    @Column(nullable = false, unique = true, length = 30)
    private String profileName;
    @Column(length = 300)
    private String bio;
    private String profileImageUrl;

    @Enumerated(EnumType.STRING)
    private Gender gender;

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
