package com.huza.huzabackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "applications")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_id", nullable = false)
    private User artist; // Artist who applied

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus status;

    @Column(name = "cover_letter", length = 2000)
    private String coverLetter;

    @Column(name = "resume_url")
    private String resumeUrl;

    @Column(name = "portfolio_url")
    private String portfolioUrl;

    @Column(name = "applied_at", nullable = false, updatable = false)
    private LocalDateTime appliedAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "reviewed_by")
    private String reviewedBy; // User ID of recruiter who reviewed

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    @Column(name = "notes", length = 1000)
    private String notes; // Internal notes from recruiter

    @PrePersist
    protected void onCreate() {
        appliedAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        status = ApplicationStatus.PENDING;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void withdraw() {
        if (this.status == ApplicationStatus.PENDING) {
            this.status = ApplicationStatus.WITHDRAWN;
        } else {
            throw new IllegalStateException("Can only withdraw PENDING applications");
        }
    }

    public void accept(String reviewerId) {
        this.status = ApplicationStatus.ACCEPTED;
        this.reviewedBy = reviewerId;
        this.reviewedAt = LocalDateTime.now();
    }

    public void reject(String reviewerId) {
        this.status = ApplicationStatus.REJECTED;
        this.reviewedBy = reviewerId;
        this.reviewedAt = LocalDateTime.now();
    }
}
