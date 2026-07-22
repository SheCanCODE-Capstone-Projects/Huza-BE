package com.huza.huzabackend.controller;

import com.huza.huzabackend.dto.ApiResponse;
import com.huza.huzabackend.dto.MessageResponse;
import com.huza.huzabackend.dto.SendMessageRequest;
import com.huza.huzabackend.entity.NotificationType;
import com.huza.huzabackend.service.MessageService;
import com.huza.huzabackend.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recruiter/messages")
@RequiredArgsConstructor
public class RecruiterMessageController {

    private final MessageService messageService;
    private final NotificationService notificationService;

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<List<MessageResponse>>> getConversation(
            Authentication authentication,
            @PathVariable String userId) {

        String email = authentication.getName();

        if ("me".equals(userId)) {
            List<MessageResponse> messages = messageService.getInboxByEmail(email);
            return ResponseEntity.ok(
                    ApiResponse.success("Inbox retrieved successfully", messages)
            );
        }

        List<MessageResponse> messages = messageService.getConversation(email, userId);
        return ResponseEntity.ok(
                ApiResponse.success("Conversation retrieved successfully", messages)
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<MessageResponse>> sendMessage(
            Authentication authentication,
            @Valid @RequestBody SendMessageRequest request) {

        String senderEmail = authentication.getName();
        MessageResponse response = messageService.sendMessage(senderEmail, request);

        // Send notification to receiver using NotificationType.MESSAGE
        notificationService.notify(
                response.getReceiverId(),
                "New Message from " + response.getSenderName(),
                response.getContent().length() > 100 ?
                        response.getContent().substring(0, 100) + "..." :
                        response.getContent(),
                "/messages/" + response.getId(),
                NotificationType.MESSAGE  // Using MESSAGE type
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Message sent successfully", response));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<ApiResponse<String>> markAsRead(
            Authentication authentication,
            @PathVariable String id) {

        String email = authentication.getName();
        messageService.markAsRead(id, email);

        return ResponseEntity.ok(
                ApiResponse.success("Message marked as read", null)
        );
    }
}