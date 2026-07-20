package com.huza.huzabackend.service;

import com.huza.huzabackend.dto.SendMessageRequest;
import com.huza.huzabackend.entity.Message;
import com.huza.huzabackend.entity.User;
import com.huza.huzabackend.repository.MessageRepository;
import com.huza.huzabackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    public Message sendMessage(String senderEmail, SendMessageRequest request){

        User sender = userRepository.findByEmail(senderEmail)
                .orElseThrow(() -> new RuntimeException("Sender not found"));

        User receiver = userRepository.findById(request.getReceiverId())
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        Message message = Message.builder()
                .sender(sender)
                .receiver(receiver)
                .content(request.getContent())
                .attachmentUrl(request.getAttachmentUrl())
                .build();

        return messageRepository.save(message);
    }

    public List<Message> getInboxByEmail(String email){

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return messageRepository.findBySenderOrReceiverOrderBySentAtDesc(user,user);
    }

    public void markAsRead(String id){

        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Message not found"));

        message.setRead(true);

        messageRepository.save(message);
    }
}