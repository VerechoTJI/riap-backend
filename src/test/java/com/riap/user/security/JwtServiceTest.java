package com.riap.user.security;

import com.riap.user.domain.model.UserRole;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    // HS256 requires at least a 256-bit (32-byte) key.
    private static final String SECRET = "test-secret-test-secret-test-secret-0123456789";

    private final JwtService jwtService = new JwtService(SECRET, 60);

    @Test
    void generatedTokenRoundTripsToUserIdAndRole() {
        UUID userId = UUID.randomUUID();

        AuthenticatedUser parsed = jwtService.parse(jwtService.generateToken(userId, UserRole.LANDLORD));

        assertEquals(userId, parsed.userId());
        assertEquals(UserRole.LANDLORD, parsed.role());
        assertNotNull(parsed.tokenId(), "token must carry a jti for revocation");
    }

    @Test
    void expiredTokenIsRejected() {
        JwtService alreadyExpired = new JwtService(SECRET, -1);
        String token = alreadyExpired.generateToken(UUID.randomUUID(), UserRole.TENANT);

        assertThrows(JwtException.class, () -> jwtService.parse(token));
    }

    @Test
    void tamperedTokenIsRejected() {
        String token = jwtService.generateToken(UUID.randomUUID(), UserRole.TENANT);
        char last = token.charAt(token.length() - 1);
        String tampered = token.substring(0, token.length() - 1) + (last == 'a' ? 'b' : 'a');

        assertThrows(JwtException.class, () -> jwtService.parse(tampered));
    }

    @Test
    void tokenSignedWithAnotherSecretIsRejected() {
        JwtService other = new JwtService("another-secret-another-secret-abcdefghijkl", 60);
        String token = other.generateToken(UUID.randomUUID(), UserRole.ADMIN);

        assertThrows(JwtException.class, () -> jwtService.parse(token));
    }
}
