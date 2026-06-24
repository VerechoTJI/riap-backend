package com.riap.user.infrastructure.rest;

import com.riap.user.application.service.AuthenticationResult;
import com.riap.user.application.service.AuthenticationService;
import com.riap.user.domain.model.UserAccountEntity;
import com.riap.user.domain.model.UserRole;
import com.riap.user.security.AuthenticatedUser;
import com.riap.user.security.JwtService;
import com.riap.user.security.RequireRole;
import com.riap.user.security.TokenBlacklist;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST endpoints for the UAS (SDD 5.2.1 {@code login}). Successful login issues a
 * signed JWT (UAS-F-01, UAS-F-03); {@code logout} revokes it (UAS-F-01); {@code me}
 * and {@code admin-area} demonstrate role-based access control via {@link RequireRole}
 * (UAS-F-02, UAS-F-04).
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;
    private final JwtService jwtService;
    private final TokenBlacklist tokenBlacklist;

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
                    .body(new LoginResponse(false, null, null, null));
        }
        String token = jwtService.generateToken(result.userId(), result.role());
        return ResponseEntity.ok(new LoginResponse(true, result.userId(), result.role(), token));
    }

    @PostMapping("/logout")
    @RequireRole
    public ResponseEntity<Void> logout(@RequestAttribute(AuthenticatedUser.ATTRIBUTE) AuthenticatedUser user) {
        tokenBlacklist.revoke(user.tokenId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    @RequireRole
    public ResponseEntity<MeResponse> me(@RequestAttribute(AuthenticatedUser.ATTRIBUTE) AuthenticatedUser user) {
        return ResponseEntity.ok(new MeResponse(user.userId(), user.role()));
    }

    /** Demonstrates role enforcement; teammates annotate their own admin endpoints the same way. */
    @GetMapping("/admin-area")
    @RequireRole(UserRole.ADMIN)
    public ResponseEntity<String> adminArea() {
        return ResponseEntity.ok("admin ok");
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
        private final String token;
    }

    @Data
    public static class MeResponse {
        private final UUID userId;
        private final UserRole role;
    }
}
