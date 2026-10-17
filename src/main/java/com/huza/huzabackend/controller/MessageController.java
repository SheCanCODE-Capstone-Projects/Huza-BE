package com.huza.huzabackend.controller;

import com.huza.huzabackend.dto.ApiResponse;
import com.huza.huzabackend.dto.MessageResponse;
import com.huza.huzabackend.dto.SendMessageRequest;
import com.huza.huzabackend.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/artist/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<MessageResponse>>> inbox(
            Authentication authentication) {

        String email = authentication.getName();

        List<MessageResponse> messages =
                messageService.getInboxByEmail(email);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Messages retrieved successfully",
                        messages
                )
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<MessageResponse>> sendMessage(
            Authentication authentication,
            @Valid @RequestBody SendMessageRequest request) {

        String senderEmail = authentication.getName();

        MessageResponse message =
                messageService.sendMessage(senderEmail, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        ApiResponse.success(
                                "Message sent successfully",
                                message
                        )
                );
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<ApiResponse<String>> markAsRead(
            Authentication authentication,
            @PathVariable String id) {


        messageService.markAsRead(id, authentication.getName());

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Message marked as read",
                        null
                )
        );
    }
}