package com.huza.huzabackend.dto;
import jakarta.validation.constraints.Size;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SendMessageRequest {

    @NotBlank(message = "Receiver Id is required")
    private String receiverId;

    @NotBlank(message = "Message content is required")
    @Size(
            max = 3000,
            message = "Message content must not exceed 3000 characters"
    )
    private String content;

    private String attachmentUrl;
}