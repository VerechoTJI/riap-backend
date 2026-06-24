package com.riap.user.application.service;

import com.riap.user.domain.model.UserAccountEntity;
import com.riap.user.domain.model.UserRole;
import com.riap.user.domain.model.UserStatus;
import com.riap.user.domain.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * User Authentication Subsystem (UAS) service: account registration and credential
 * verification (UAS-F-01). Passwords are only ever stored and compared as one-way
 * BCrypt hashes (UAS-N-09). Persistence goes through {@link UserAccountRepository}
 * (Spring Data JPA), keeping the UAS off direct table access.
 */
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserAccountRepository userAccounts;
    private final PasswordEncoder passwordEncoder;

    /**
     * Registers a new active account with a hashed password (UAS-F-01, UAS-N-09).
     *
     * @throws IllegalStateException if the login identifier is already taken.
     */
    @Transactional
    public UserAccountEntity register(String loginIdentifier, String rawPassword, UserRole role) {
        if (userAccounts.existsByLoginIdentifier(loginIdentifier)) {
            throw new IllegalStateException("loginIdentifier already registered: " + loginIdentifier);
        }
        UserAccountEntity account = UserAccountEntity.builder()
                .loginIdentifier(loginIdentifier)
                .passwordHash(passwordEncoder.encode(rawPassword))
                .role(role)
                .status(UserStatus.ACTIVE)
                .build();
        return userAccounts.save(account);
    }

    /**
     * Verifies a login identifier / password pair (UAS-F-01). An unknown account and
     * a wrong password both yield {@link AuthenticationResult.Outcome#INVALID_CREDENTIALS}
     * so the response does not leak which accounts exist.
     */
    @Transactional(readOnly = true)
    public AuthenticationResult authenticate(String loginIdentifier, String rawPassword) {
        Optional<UserAccountEntity> found = userAccounts.findByLoginIdentifier(loginIdentifier);
        if (found.isEmpty()) {
            return AuthenticationResult.failure(AuthenticationResult.Outcome.INVALID_CREDENTIALS);
        }

        UserAccountEntity account = found.get();
        switch (account.getStatus()) {
            case LOCKED:
                return AuthenticationResult.failure(AuthenticationResult.Outcome.ACCOUNT_LOCKED);
            case DISABLED:
                return AuthenticationResult.failure(AuthenticationResult.Outcome.ACCOUNT_DISABLED);
            case ACTIVE:
            default:
                break;
        }

        if (!passwordEncoder.matches(rawPassword, account.getPasswordHash())) {
            return AuthenticationResult.failure(AuthenticationResult.Outcome.INVALID_CREDENTIALS);
        }
        return AuthenticationResult.success(account.getId(), account.getRole());
    }
}
