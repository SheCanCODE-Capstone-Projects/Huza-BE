package com.huza.huzabackend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "consents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Consent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruiter_id", nullable = false)
    private User recruiter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_id", nullable = false)
    private User artist;

    @Column(nullable = false)
    private BigDecimal agreedSalary;

    @Column(nullable = false)
    private LocalDateTime startDate;

    private LocalDateTime endDate;

    @Column(columnDefinition = "TEXT")
    private String terms;

    @Column(columnDefinition = "TEXT")
    private String specialConditions;

    @Enumerated(EnumType.STRING)
    private ApprovalStatus approvalStatus;  // PENDING, APPROVED, REJECTED

    @Enumerated(EnumType.STRING)
    private ConsentStatus status;  // DRAFT, SENT, SIGNED_BY_ARTIST, etc.

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private String approvedBy;  // Manager who approved

    private LocalDateTime approvedAt;

    @PrePersist
    protected void onCreate() {
        if (approvalStatus == null) {
            approvalStatus = ApprovalStatus.PENDING;
        }
        if (status == null) {
            status = ConsentStatus.DRAFT;
        }
    }
}