package com.riap.pbi.rcs.infrastructure.security;

import com.riap.pbi.rcs.port.AuthenticationProvider;
import com.riap.user.security.AuthenticatedUser;
import com.riap.user.security.JwtService;
import org.springframework.stereotype.Component;

@Component
public class JwtAuthenticationAdapter implements AuthenticationProvider {

    private final JwtService jwtService;

    public JwtAuthenticationAdapter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public String validateTokenAndGetUserId(String token) {
        try {
            AuthenticatedUser user = jwtService.parse(token);
            return user.userId().toString();
        } catch (Exception e) {
            return null; // Invalid or missing token
        }
    }
}
