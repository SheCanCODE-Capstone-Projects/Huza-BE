package com.huza.huzabackend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    @Column(nullable = false, length = 3000)
    private String content;

    @Column(length = 1000)
    private String attachmentUrl;

    @Column(name = "is_read")
    private boolean isRead;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @PrePersist
    public void onCreate() {
        sentAt = LocalDateTime.now();
        isRead = false;
    }
}