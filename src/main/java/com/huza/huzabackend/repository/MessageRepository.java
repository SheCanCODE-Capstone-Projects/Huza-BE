package com.huza.huzabackend.repository;

import com.huza.huzabackend.entity.Message;
import com.huza.huzabackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, String> {

    List<Message> findBySenderOrReceiverOrderBySentAtDesc(User sender, User receiver);

}