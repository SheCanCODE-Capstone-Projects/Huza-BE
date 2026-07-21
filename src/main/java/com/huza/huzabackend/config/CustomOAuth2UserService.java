package com.huza.huzabackend.config;

import com.huza.huzabackend.entity.User;
import com.huza.huzabackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String email = oAuth2User.getAttribute("email");
        if (email == null) {
            throw new OAuth2AuthenticationException(
                    new OAuth2Error("email_not_found"),
                    "Google account did not return an email address");
        }

        Optional<User> existingUser = userRepository.findByEmailIgnoreCase(email);
        if (existingUser.isEmpty()) {
            log.warn("Google login attempt for unregistered email: {}", email);
            // This error code is what OAuth2LoginFailureHandler below checks for
            throw new OAuth2AuthenticationException(
                    new OAuth2Error("user_not_found"),
                    "No account found for " + email);
        }

        // Attach your own user id to the attributes so the success handler
        // doesn't have to look it up again
        Map<String, Object> attributes = new HashMap<>(oAuth2User.getAttributes());
        attributes.put("huzaUserId", existingUser.get().getId());
        attributes.put("huzaRole", existingUser.get().getRole().name());
        attributes.put("huzaFullName", existingUser.get().getFullName());

        return new org.springframework.security.oauth2.core.user.DefaultOAuth2User(
                oAuth2User.getAuthorities(),
                attributes,
                "email");
    }
}
