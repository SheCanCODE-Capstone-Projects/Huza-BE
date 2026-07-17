package com.huza.huzabackend.service;

import com.huza.huzabackend.dto.LoginRequest;
import com.huza.huzabackend.dto.LoginResponse;
import com.huza.huzabackend.entity.Role;
import com.huza.huzabackend.entity.User;
import com.huza.huzabackend.entity.UserStatus;
import com.huza.huzabackend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceLoginTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private UserService userService;

    @Test
    void loginShouldAcceptLegacyPlainTextPasswordAndRehashIt() {
        User user = User.builder()
                .id("user-1")
                .email("jacaci2075@gicont.com")
                .fullName("Jac aci")
                .password("Rwanda@123")
                .role(Role.ARTIST)
                .status(UserStatus.ACTIVE)
                .isVerified(true)
                .build();

        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("bad creds"));
        when(userRepository.findByEmail("jacaci2075@gicont.com")).thenReturn(Optional.of(user));
        when(userRepository.findById("user-1")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("Rwanda@123", "Rwanda@123")).thenReturn(true);
        when(passwordEncoder.encode("Rwanda@123")).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(jwtService.generateToken(any(User.class))).thenReturn("jwt-token");

        LoginResponse response = userService.login(new LoginRequest("jacaci2075@gicont.com", "Rwanda@123"));

        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        assertEquals("encoded-password", user.getPassword());
        verify(userRepository, org.mockito.Mockito.atLeastOnce()).save(any(User.class));
    }
}
