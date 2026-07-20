package com.huza.huzabackend.controller;

import com.huza.huzabackend.dto.ApiResponse;
import com.huza.huzabackend.dto.NotificationResponse;
import com.huza.huzabackend.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/artist/notifications")
@RequiredArgsConstructor
public class NotificationController {


    private final NotificationService notificationService;


    @GetMapping
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getNotifications(
            Authentication authentication) {


        String email = authentication.getName();


        List<NotificationResponse> notifications =
                notificationService.getNotificationsByEmail(email);


        return ResponseEntity.ok(
                ApiResponse.success(
                        "Notifications retrieved successfully",
                        notifications
                )
        );
    }



    @PutMapping("/{id}/read")
    public ResponseEntity<ApiResponse<String>> markAsRead(
            @PathVariable String id) {


        notificationService.markAsRead(id);


        return ResponseEntity.ok(
                ApiResponse.success(
                        "Notification marked as read",
                        null
                )
        );
    }
}