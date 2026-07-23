package com.huza.huzabackend.dto;

import com.huza.huzabackend.entity.NotificationType;
import lombok.Builder;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

@Data
@Builder
public class NotificationResponse {

    private String id;

    private String title;

    private String message;

    private String link;

    private NotificationType type;

    @JsonProperty("isRead")
    private boolean isRead;

    private LocalDateTime createdAt;
}