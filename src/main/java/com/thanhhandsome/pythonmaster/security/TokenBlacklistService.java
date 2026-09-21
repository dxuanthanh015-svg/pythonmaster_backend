package com.thanhhandsome.pythonmaster.security;

import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Lưu các JWT đã logout cho đến khi hết hạn tự nhiên.
 * In-memory — đủ cho app admin single-instance.
 */
@Service
public class TokenBlacklistService {

    /** token -> epoch millis hết hạn */
    private final ConcurrentHashMap<String, Long> revoked = new ConcurrentHashMap<>();

    public void revoke(String token, long expiresAtEpochMs) {
        if (token == null || token.isBlank()) {
            return;
        }
        revoked.put(token, expiresAtEpochMs);
        cleanupExpired();
    }

    public boolean isRevoked(String token) {
        if (token == null) {
            return false;
        }
        Long expiresAt = revoked.get(token);
        if (expiresAt == null) {
            return false;
        }
        if (expiresAt <= System.currentTimeMillis()) {
            revoked.remove(token);
            return false;
        }
        return true;
    }

    private void cleanupExpired() {
        long now = System.currentTimeMillis();
        Iterator<Map.Entry<String, Long>> it = revoked.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Long> e = it.next();
            if (e.getValue() <= now) {
                it.remove();
            }
        }
    }
}
