package com.huza.huzabackend.service;

import com.huza.huzabackend.dto.NotificationResponse;
import com.huza.huzabackend.entity.Notification;
import com.huza.huzabackend.entity.User;
import com.huza.huzabackend.entity.NotificationType;
import com.huza.huzabackend.repository.NotificationRepository;
import com.huza.huzabackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public void notify(String userId,
                       String title,
                       String message,
                       String link,
                       NotificationType type){

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Notification notification = Notification.builder()
                .user(user)
                .title(title)
                .message(message)
                .link(link)
                .type(type)
                .build();

        notificationRepository.save(notification);
    }

    public List<NotificationResponse> getNotificationsByEmail(String email){

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return notificationRepository.findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(notification -> NotificationResponse.builder()
                        .id(notification.getId())
                        .title(notification.getTitle())
                        .message(notification.getMessage())
                        .link(notification.getLink())
                        .type(notification.getType())
                        .isRead(notification.isRead())
                        .createdAt(notification.getCreatedAt())
                        .build())
                .toList();
    }

    public void markAsRead(String id, String email) {

        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Notification not found"));

        if (!notification.getUser().getEmail().equals(email)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You are not allowed to modify this notification.");
        }

        notification.setRead(true);

        notificationRepository.save(notification);
    }
}