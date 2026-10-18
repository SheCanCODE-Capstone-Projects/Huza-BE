package com.huza.huzabackend.repository;

import com.huza.huzabackend.entity.Notification;
import com.huza.huzabackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, String> {

    List<Notification> findByUserOrderByCreatedAtDesc(User user);

}