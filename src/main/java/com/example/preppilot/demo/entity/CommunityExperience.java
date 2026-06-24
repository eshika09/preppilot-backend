package com.example.preppilot.demo.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "community_experiences")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommunityExperience {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String companyName;

    @Column(nullable = false)
    private String role;

    // OA_ROUND, INTERVIEW_ROUND_1, INTERVIEW_ROUND_2, HR, etc.
    @Column(nullable = false)
    private String round;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String questionsAsked;

    @Column(columnDefinition = "TEXT")
    private String tips;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExperienceOutcome outcome;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    // logged in users can post, but anyone can read
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

}
