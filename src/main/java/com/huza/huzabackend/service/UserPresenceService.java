package com.huza.huzabackend.service;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserPresenceService {

    // User active timeout in seconds (5 minutes = 300 seconds)
    private static final long ONLINE_TIMEOUT_SECONDS = 300;

    private final Map<String, Instant> lastActiveMap = new ConcurrentHashMap<>();

    /**
     * Record user activity / heartbeat
     */
    public void recordActivity(String userId) {
        if (userId != null && !userId.isBlank()) {
            lastActiveMap.put(userId.trim(), Instant.now());
        }
    }

    /**
     * Check if a user is currently considered online
     */
    public boolean isUserOnline(String userId) {
        if (userId == null) {
            return false;
        }
        Instant lastActive = lastActiveMap.get(userId.trim());
        if (lastActive == null) {
            return false;
        }
        return lastActive.isAfter(Instant.now().minusSeconds(ONLINE_TIMEOUT_SECONDS));
    }

    /**
     * Get user's last active timestamp
     */
    public Instant getLastSeen(String userId) {
        if (userId == null) {
            return null;
        }
        return lastActiveMap.get(userId.trim());
    }
}
