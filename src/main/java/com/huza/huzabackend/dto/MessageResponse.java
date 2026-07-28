package com.huza.huzabackend.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class MessageResponse {

    private String id;

    private String senderId;
    private String senderName;
    private String senderEmail;
    private String senderProfilePicture;
    private boolean senderOnline;

    private String receiverId;
    private String receiverName;
    private String receiverEmail;
    private String receiverProfilePicture;
    private boolean receiverOnline;

    private String content;

    private String attachmentUrl;

    private boolean isRead;

    private LocalDateTime sentAt;
}