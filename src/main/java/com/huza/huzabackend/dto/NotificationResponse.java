package com.huza.huzabackend.dto;

import com.huza.huzabackend.entity.NotificationType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class NotificationResponse {

    private String id;

    private String title;

    private String message;

    private String link;

    private NotificationType type;

    private boolean isRead;

    private LocalDateTime createdAt;
}