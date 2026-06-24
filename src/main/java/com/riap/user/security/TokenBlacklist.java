package com.riap.user.security;

import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Tracks revoked token ids ({@code jti}) so logout invalidates a token server-side
 * (UAS-F-01 logout). In-memory only: revocations do not survive a restart and are not
 * shared across instances — a Redis/DB-backed store would be needed for production.
 */
@Component
public class TokenBlacklist {

    private final Set<String> revokedTokenIds = ConcurrentHashMap.newKeySet();

    public void revoke(String tokenId) {
        revokedTokenIds.add(tokenId);
    }

    public boolean isRevoked(String tokenId) {
        return revokedTokenIds.contains(tokenId);
    }
}
