package com.huza.huzabackend.service;

import com.huza.huzabackend.dto.SendMessageRequest;
import com.huza.huzabackend.entity.Message;
import com.huza.huzabackend.entity.User;
import com.huza.huzabackend.repository.MessageRepository;
import com.huza.huzabackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.huza.huzabackend.dto.MessageResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    public MessageResponse sendMessage(String senderEmail,
                                       SendMessageRequest request) {

        User sender = userRepository.findByEmail(senderEmail)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Sender not found"));

        User receiver = userRepository.findById(request.getReceiverId())
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Receiver not found"));

        Message message = Message.builder()
                .sender(sender)
                .receiver(receiver)
                .content(request.getContent())
                .attachmentUrl(request.getAttachmentUrl())
                .build();

        Message saved = messageRepository.save(message);

        return MessageResponse.builder()
                .id(saved.getId())
                .senderId(sender.getId())
                .senderName(sender.getFullName())
                .senderEmail(sender.getEmail())
                .receiverId(receiver.getId())
                .receiverName(receiver.getFullName())
                .receiverEmail(receiver.getEmail())
                .content(saved.getContent())
                .attachmentUrl(saved.getAttachmentUrl())
                .isRead(saved.isRead())
                .sentAt(saved.getSentAt())
                .build();
    }
    @Transactional(readOnly = true)
    public List<MessageResponse> getInboxByEmail(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "User not found"));

        return messageRepository
                .findBySenderOrReceiverOrderBySentAtDesc(user, user)
                .stream()
                .map(message -> MessageResponse.builder()
                        .id(message.getId())
                        .senderId(message.getSender().getId())
                        .senderName(message.getSender().getFullName())
                        .senderEmail(message.getSender().getEmail())
                        .receiverId(message.getReceiver().getId())
                        .receiverName(message.getReceiver().getFullName())
                        .receiverEmail(message.getReceiver().getEmail())
                        .content(message.getContent())
                        .attachmentUrl(message.getAttachmentUrl())
                        .isRead(message.isRead())
                        .sentAt(message.getSentAt())
                        .build())
                .toList();
    }
    @Transactional
    public void markAsRead(String id,
                           String email) {

        Message message = messageRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Message not found"));

        if (!message.getReceiver().getEmail().equals(email)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You are not allowed to mark this message as read.");
        }

        message.setRead(true);

        messageRepository.save(message);
    }
}