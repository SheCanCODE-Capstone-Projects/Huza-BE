package com.huza.huzabackend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SendMessageRequest {

    @NotBlank(message = "Receiver Id is required")
    private String receiverId;

    @NotBlank(message = "Message content is required")
    private String content;

    private String attachmentUrl;
}