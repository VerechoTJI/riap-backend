package com.riap.user.application.service;

import com.riap.user.domain.model.UserAccountEntity;
import com.riap.user.domain.model.UserRole;
import com.riap.user.domain.model.UserStatus;
import com.riap.user.domain.repository.UserAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthenticationServiceTest {

    @Mock
    private UserAccountRepository userAccounts;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authenticationService = new AuthenticationService(userAccounts, passwordEncoder);
    }

    private UserAccountEntity account(String login, String rawPassword, UserStatus status) {
        return UserAccountEntity.builder()
                .id(UUID.randomUUID())
                .loginIdentifier(login)
                .passwordHash(passwordEncoder.encode(rawPassword))
                .role(UserRole.TENANT)
                .status(status)
                .build();
    }

    @Test
    void register_ShouldHashPasswordAndSave_WhenLoginIsFree() {
        when(userAccounts.existsByLoginIdentifier("new@example.com")).thenReturn(false);
        when(userAccounts.save(any())).thenAnswer(inv -> inv.getArgument(0));

        UserAccountEntity saved = authenticationService.register("new@example.com", "secret", UserRole.LANDLORD);

        assertNotEquals("secret", saved.getPasswordHash(), "password must not be stored in clear text");
        assertTrue(passwordEncoder.matches("secret", saved.getPasswordHash()));
        assertEquals(UserRole.LANDLORD, saved.getRole());
        assertEquals(UserStatus.ACTIVE, saved.getStatus());
        verify(userAccounts, times(1)).save(any());
    }

    @Test
    void register_ShouldThrow_WhenLoginAlreadyTaken() {
        when(userAccounts.existsByLoginIdentifier("dup@example.com")).thenReturn(true);

        assertThrows(IllegalStateException.class,
                () -> authenticationService.register("dup@example.com", "secret", UserRole.TENANT));
        verify(userAccounts, never()).save(any());
    }

    @Test
    void authenticate_ShouldSucceed_WithCorrectPassword() {
        UserAccountEntity acct = account("user@example.com", "correct", UserStatus.ACTIVE);
        when(userAccounts.findByLoginIdentifier("user@example.com")).thenReturn(Optional.of(acct));

        AuthenticationResult result = authenticationService.authenticate("user@example.com", "correct");

        assertTrue(result.isSuccess());
        assertEquals(acct.getId(), result.userId());
        assertEquals(UserRole.TENANT, result.role());
    }

    @Test
    void authenticate_ShouldFailInvalid_WithWrongPassword() {
        when(userAccounts.findByLoginIdentifier("user@example.com"))
                .thenReturn(Optional.of(account("user@example.com", "correct", UserStatus.ACTIVE)));

        AuthenticationResult result = authenticationService.authenticate("user@example.com", "wrong");

        assertEquals(AuthenticationResult.Outcome.INVALID_CREDENTIALS, result.outcome());
        assertNull(result.userId());
    }

    @Test
    void authenticate_ShouldFailInvalid_WhenAccountUnknown() {
        when(userAccounts.findByLoginIdentifier("ghost@example.com")).thenReturn(Optional.empty());

        AuthenticationResult result = authenticationService.authenticate("ghost@example.com", "whatever");

        assertEquals(AuthenticationResult.Outcome.INVALID_CREDENTIALS, result.outcome());
    }

    @Test
    void authenticate_ShouldReportLocked_AndNotCheckPassword() {
        when(userAccounts.findByLoginIdentifier("locked@example.com"))
                .thenReturn(Optional.of(account("locked@example.com", "correct", UserStatus.LOCKED)));

        AuthenticationResult result = authenticationService.authenticate("locked@example.com", "correct");

        assertEquals(AuthenticationResult.Outcome.ACCOUNT_LOCKED, result.outcome());
    }

    @Test
    void authenticate_ShouldReportDisabled() {
        when(userAccounts.findByLoginIdentifier("disabled@example.com"))
                .thenReturn(Optional.of(account("disabled@example.com", "correct", UserStatus.DISABLED)));

        AuthenticationResult result = authenticationService.authenticate("disabled@example.com", "correct");

        assertEquals(AuthenticationResult.Outcome.ACCOUNT_DISABLED, result.outcome());
    }
}
