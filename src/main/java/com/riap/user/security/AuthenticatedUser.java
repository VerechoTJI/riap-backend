package com.riap.user.security;

import com.riap.user.domain.model.UserRole;

import java.util.UUID;

/**
 * The authenticated principal carried by a verified JWT: the user's id, role, and
 * the token's unique id ({@code jti}) used for logout/revocation.
 */
public record AuthenticatedUser(UUID userId, UserRole role, String tokenId) {

    /** Request attribute key under which the interceptor exposes the principal. */
    public static final String ATTRIBUTE = "uas.authenticatedUser";
}
