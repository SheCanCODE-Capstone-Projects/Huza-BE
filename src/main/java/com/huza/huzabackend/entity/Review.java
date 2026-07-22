package com.huza.huzabackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * ERD: REVIEW — written after a completed job, linked 1:1 to CONSENT.
 * Extra moderationStatus supports admin approve/reject before the review is visible.
 */
@Entity
@Table(name = "reviews")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long reviewId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewer_id", nullable = false)
    private User reviewer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_user_id", nullable = false)
    private User reviewedUser;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consent_id", nullable = false, unique = true)
    private Consent consent;

    @Column(nullable = false)
    private Integer rating;

    @Column(columnDefinition = "TEXT")
    private String comment;

    @Column(name = "review_date", nullable = false)
    private LocalDateTime reviewDate;

    /** Admin moderation gate — not on ERD but required for review moderation APIs. */
    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(name = "moderation_status", nullable = false)
    private ModerationStatus moderationStatus = ModerationStatus.PENDING;

    public enum ModerationStatus {
        PENDING, APPROVED, REJECTED
    }

    @PrePersist
    protected void onCreate() {
        if (this.reviewDate == null) {
            this.reviewDate = LocalDateTime.now();
        }
        if (this.moderationStatus == null) {
            this.moderationStatus = ModerationStatus.PENDING;
        }
    }
}
