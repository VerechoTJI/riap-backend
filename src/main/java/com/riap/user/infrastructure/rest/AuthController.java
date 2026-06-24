package com.riap.user.infrastructure.rest;

import com.riap.user.application.service.AuthenticationResult;
import com.riap.user.application.service.AuthenticationService;
import com.riap.user.domain.model.UserAccountEntity;
import com.riap.user.domain.model.UserRole;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST endpoints for the UAS (SDD 5.2.1 {@code login}). Token issuance is not yet
 * implemented; {@code login} returns the authenticated user's id and role so the
 * client can drive role-based routing (UAS-F-03). JWT/session tokens are a follow-up.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest request) {
        UserAccountEntity account =
                authenticationService.register(request.getAccount(), request.getPassword(), request.getRole());
        return ResponseEntity.status(HttpStatus.CREATED).body(new RegisterResponse(account.getId()));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        AuthenticationResult result =
                authenticationService.authenticate(request.getAccount(), request.getPassword());
        if (!result.isSuccess()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginResponse(false, null, null));
        }
        return ResponseEntity.ok(new LoginResponse(true, result.userId(), result.role()));
    }

    @Data
    public static class RegisterRequest {
        private String account;
        private String password;
        private UserRole role;
    }

    @Data
    public static class RegisterResponse {
        private final UUID userId;
    }

    @Data
    public static class LoginRequest {
        private String account;
        private String password;
    }

    @Data
    public static class LoginResponse {
        private final boolean success;
        private final UUID userId;
        private final UserRole role;
    }
}
