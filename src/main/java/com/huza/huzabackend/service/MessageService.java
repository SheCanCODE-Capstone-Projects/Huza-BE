package com.huza.huzabackend.service;

import com.huza.huzabackend.dto.ChatConversationResponse;
import com.huza.huzabackend.dto.MessageResponse;
import com.huza.huzabackend.dto.SendMessageRequest;
import com.huza.huzabackend.entity.Message;
import com.huza.huzabackend.entity.NotificationType;
import com.huza.huzabackend.entity.User;
import com.huza.huzabackend.repository.MessageRepository;
import com.huza.huzabackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final UserPresenceService userPresenceService;
    private final NotificationService notificationService;

    @Transactional
    public MessageResponse sendMessage(String senderEmail, SendMessageRequest request) {
        User sender = userRepository.findByEmail(senderEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sender not found"));

        User receiver = userRepository.findById(request.getReceiverId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Receiver not found"));

        userPresenceService.recordActivity(sender.getId());

        Message message = Message.builder()
                .sender(sender)
                .receiver(receiver)
                .content(request.getContent())
                .attachmentUrl(request.getAttachmentUrl())
                .build();

        Message saved = messageRepository.save(message);

        String preview = saved.getContent() != null ? saved.getContent() : "[Attachment]";
        if (preview.length() > 100) {
            preview = preview.substring(0, 100) + "...";
        }

        notificationService.notify(
                receiver.getId(),
                "New Message from " + sender.getFullName(),
                preview,
                "/messages/" + saved.getId(),
                NotificationType.MESSAGE
        );

        return mapToResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<MessageResponse> getInboxByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        userPresenceService.recordActivity(user.getId());

        return messageRepository.findBySenderOrReceiverOrderBySentAtDesc(user, user)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    public List<MessageResponse> getConversation(String userEmail, String otherUserId) {
        User currentUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        User otherUser = userRepository.findById(otherUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Other user not found"));

        userPresenceService.recordActivity(currentUser.getId());

        // Automatically mark unread incoming messages in this conversation as read
        List<Message> unread = messageRepository.findBySenderAndReceiverAndIsReadFalse(otherUser, currentUser);
        if (!unread.isEmpty()) {
            for (Message m : unread) {
                m.setRead(true);
            }
            messageRepository.saveAll(unread);
        }

        return messageRepository.findConversationBetweenUsers(currentUser, otherUser)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ChatConversationResponse> getConversationsList(String userEmail) {
        User currentUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        userPresenceService.recordActivity(currentUser.getId());

        List<Message> allUserMessages = messageRepository.findBySenderOrReceiverOrderBySentAtDesc(currentUser, currentUser);

        // Group messages by partner user ID keeping insertion (time desc) order
        Map<String, User> partners = new LinkedHashMap<>();
        Map<String, Message> latestMessages = new HashMap<>();

        for (Message msg : allUserMessages) {
            User partner = msg.getSender().getId().equals(currentUser.getId()) ? msg.getReceiver() : msg.getSender();
            String partnerId = partner.getId();

            if (!partners.containsKey(partnerId)) {
                partners.put(partnerId, partner);
                latestMessages.put(partnerId, msg);
            }
        }

        List<ChatConversationResponse> conversationResponses = new ArrayList<>();
        for (Map.Entry<String, User> entry : partners.entrySet()) {
            String partnerId = entry.getKey();
            User partner = entry.getValue();
            Message lastMsg = latestMessages.get(partnerId);

            long unreadCount = messageRepository.countBySenderAndReceiverAndIsReadFalse(partner, currentUser);
            boolean isOnline = userPresenceService.isUserOnline(partnerId);

            conversationResponses.add(ChatConversationResponse.builder()
                    .userId(partner.getId())
                    .fullName(partner.getFullName())
                    .email(partner.getEmail())
                    .profilePicture(partner.getProfilePicture())
                    .role(partner.getRole() != null ? partner.getRole().name() : null)
                    .lastMessage(lastMsg.getContent())
                    .lastMessageAttachmentUrl(lastMsg.getAttachmentUrl())
                    .lastMessageSentAt(lastMsg.getSentAt())
                    .unreadCount(unreadCount)
                    .isOnline(isOnline)
                    .lastSeen(userPresenceService.getLastSeen(partnerId))
                    .build());
        }

        return conversationResponses;
    }

    @Transactional
    public void markAsRead(String messageId, String userEmail) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Message not found"));

        if (!message.getReceiver().getEmail().equals(userEmail)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to mark this message as read.");
        }

        message.setRead(true);
        messageRepository.save(message);
    }

    @Transactional
    public void markConversationAsRead(String userEmail, String otherUserId) {
        User currentUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        User otherUser = userRepository.findById(otherUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Other user not found"));

        List<Message> unreadMessages = messageRepository.findBySenderAndReceiverAndIsReadFalse(otherUser, currentUser);
        for (Message msg : unreadMessages) {
            msg.setRead(true);
        }
        messageRepository.saveAll(unreadMessages);
    }

    private MessageResponse mapToResponse(Message message) {
        boolean senderOnline = userPresenceService.isUserOnline(message.getSender().getId());
        boolean receiverOnline = userPresenceService.isUserOnline(message.getReceiver().getId());

        return MessageResponse.builder()
                .id(message.getId())
                .senderId(message.getSender().getId())
                .senderName(message.getSender().getFullName())
                .senderEmail(message.getSender().getEmail())
                .senderProfilePicture(message.getSender().getProfilePicture())
                .senderOnline(senderOnline)
                .receiverId(message.getReceiver().getId())
                .receiverName(message.getReceiver().getFullName())
                .receiverEmail(message.getReceiver().getEmail())
                .receiverProfilePicture(message.getReceiver().getProfilePicture())
                .receiverOnline(receiverOnline)
                .content(message.getContent())
                .attachmentUrl(message.getAttachmentUrl())
                .isRead(message.isRead())
                .sentAt(message.getSentAt())
                .build();
    }
}