package com.huza.huzabackend.repository;

import com.huza.huzabackend.entity.Message;
import com.huza.huzabackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, String> {

    List<Message> findBySenderOrReceiverOrderBySentAtDesc(User sender, User receiver);



    @Query("SELECT m FROM Message m " +
            "LEFT JOIN FETCH m.sender " +
            "LEFT JOIN FETCH m.receiver " +
            "WHERE (m.sender = :user1 AND m.receiver = :user2) " +
            "OR (m.sender = :user2 AND m.receiver = :user1) " +
            "ORDER BY m.sentAt ASC")
    List<Message> findConversationBetweenUsers(@Param("user1") User user1, @Param("user2") User user2);
}