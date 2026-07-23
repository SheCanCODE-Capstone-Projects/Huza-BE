package com.huza.huzabackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatConversationResponse {

    private String userId;
    private String fullName;
    private String email;
    private String profilePicture;
    private String role;

    private String lastMessage;
    private String lastMessageAttachmentUrl;
    private LocalDateTime lastMessageSentAt;

    private long unreadCount;

    private boolean isOnline;
    private Instant lastSeen;
}
