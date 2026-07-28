package com.huza.huzabackend.controller;

import com.huza.huzabackend.dto.ApiResponse;
import com.huza.huzabackend.dto.ChatConversationResponse;
import com.huza.huzabackend.dto.MessageResponse;
import com.huza.huzabackend.dto.SendMessageRequest;
import com.huza.huzabackend.service.MessageService;
import com.huza.huzabackend.service.UserPresenceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ARTIST', 'RECRUITER', 'ADMIN')")
public class ChatController {

    private final MessageService messageService;
    private final UserPresenceService userPresenceService;

    /**
     * Get all active conversations for current user with recipient online status and unread counts
     */
    @GetMapping("/conversations")
    public ResponseEntity<ApiResponse<List<ChatConversationResponse>>> getConversations(Authentication authentication) {
        String email = authentication.getName();
        List<ChatConversationResponse> conversations = messageService.getConversationsList(email);
        return ResponseEntity.ok(ApiResponse.success("Conversations retrieved successfully", conversations));
    }

    /**
     * Get full conversation history between current user and target userId
     */
    @GetMapping("/conversation/{userId}")
    public ResponseEntity<ApiResponse<List<MessageResponse>>> getConversation(
            Authentication authentication,
            @PathVariable String userId) {
        String email = authentication.getName();
        List<MessageResponse> conversation = messageService.getConversation(email, userId);
        return ResponseEntity.ok(ApiResponse.success("Conversation history retrieved successfully", conversation));
    }

    /**
     * Legacy/convenience endpoint to get conversation or inbox
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<MessageResponse>>> inbox(Authentication authentication) {
        String email = authentication.getName();
        List<MessageResponse> messages = messageService.getInboxByEmail(email);
        return ResponseEntity.ok(ApiResponse.success("Inbox retrieved successfully", messages));
    }

    /**
     * Send a new message (text and/or attachmentUrl)
     */
    @PostMapping
    public ResponseEntity<ApiResponse<MessageResponse>> sendMessage(
            Authentication authentication,
            @Valid @RequestBody SendMessageRequest request) {
        String senderEmail = authentication.getName();
        MessageResponse response = messageService.sendMessage(senderEmail, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Message sent successfully", response));
    }

    /**
     * Upload an image/file attachment and get its URL for sending in a message
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadAttachment(
            Authentication authentication,
            @RequestParam("file") MultipartFile file) throws IOException {
        String email = authentication.getName();
        if (email != null) {
            // Heartbeat
            userPresenceService.recordActivity(email);
        }

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("File cannot be empty"));
        }

        String contentType = file.getContentType() != null ? file.getContentType() : "application/octet-stream";
        String base64 = Base64.getEncoder().encodeToString(file.getBytes());
        String dataUrl = "data:" + contentType + ";base64," + base64;

        return ResponseEntity.ok(ApiResponse.success("File uploaded successfully", Map.of(
                "attachmentUrl", dataUrl,
                "filename", file.getOriginalFilename() != null ? file.getOriginalFilename() : "attachment"
        )));
    }

    /**
     * Mark single message as read
     */
    @PutMapping("/{id}/read")
    public ResponseEntity<ApiResponse<String>> markAsRead(
            Authentication authentication,
            @PathVariable String id) {
        messageService.markAsRead(id, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Message marked as read", null));
    }

    /**
     * Mark all messages in a conversation as read
     */
    @PutMapping("/conversation/{userId}/read")
    public ResponseEntity<ApiResponse<String>> markConversationAsRead(
            Authentication authentication,
            @PathVariable String userId) {
        messageService.markConversationAsRead(authentication.getName(), userId);
        return ResponseEntity.ok(ApiResponse.success("Conversation marked as read", null));
    }

    /**
     * Heartbeat endpoint for active user presence
     */
    @PostMapping("/presence/heartbeat")
    public ResponseEntity<ApiResponse<String>> heartbeat(Authentication authentication) {
        userPresenceService.recordActivity(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Heartbeat recorded", "OK"));
    }

    /**
     * Check presence (online/offline status) of a user
     */
    @GetMapping("/presence/{userId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getPresence(@PathVariable String userId) {
        boolean isOnline = userPresenceService.isUserOnline(userId);
        Instant lastSeen = userPresenceService.getLastSeen(userId);
        return ResponseEntity.ok(ApiResponse.success("User presence retrieved", Map.of(
                "userId", userId,
                "isOnline", isOnline,
                "lastSeen", lastSeen != null ? lastSeen.toString() : "Unknown"
        )));
    }
}
