package com.huza.huzabackend.repository;

import com.huza.huzabackend.entity.Review;
import com.huza.huzabackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, String> {

    List<Review> findByReviewedUser(User reviewedUser);

}