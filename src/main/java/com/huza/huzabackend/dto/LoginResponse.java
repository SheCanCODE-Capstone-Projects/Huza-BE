package com.huza.huzabackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    private String token;

    private String email;

    private String fullName;

    private String role;

    private long expiresIn; // Useful if you want to send token lifespan (in milliseconds) back to the client
}