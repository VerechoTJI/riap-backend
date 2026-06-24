package com.riap.pbi.rcs.infrastructure.fake;

import com.riap.pbi.rcs.port.AuthenticationProvider;
import org.springframework.stereotype.Component;

@Component
public class FakeAuthenticationProvider implements AuthenticationProvider {
    @Override
    public String validateTokenAndGetUserId(String token) {
        // For E2E testing, we simply treat the token as the userId.
        // e.g., token "tenant123" -> userId "tenant123"
        // Return null if token is completely missing or invalid.
        if (token == null || token.trim().isEmpty()) {
            return null;
        }
        return token;
    }
}
