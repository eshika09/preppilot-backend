package com.example.preppilot.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "resource_bookmarks",
        uniqueConstraints = {
                // prevents same resource being bookmarked to same company twice
                @UniqueConstraint(columnNames = {"resource_id", "company_id"})
        }
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResourceBookmark {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resource_id", nullable = false)
    private Resource resource;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDateTime bookmarkedAt;

    @PrePersist
    public void prePersist() {
        this.bookmarkedAt = LocalDateTime.now();
    }
}
