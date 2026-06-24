package com.riap.user.domain.repository;

import com.riap.user.domain.model.UserAccountEntity;
import com.riap.user.domain.model.UserRole;
import com.riap.user.domain.model.UserStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Persistence slice for the UAS account store (Spring Data JPA on H2). Verifies the
 * derived lookups and the unique login-identifier constraint (DBMS integrity for the
 * user_accounts table).
 */
@DataJpaTest
class UserAccountRepositoryTest {

    @Autowired
    private UserAccountRepository userAccounts;

    private UserAccountEntity account(String login) {
        return UserAccountEntity.builder()
                .loginIdentifier(login)
                .passwordHash("hash")
                .role(UserRole.TENANT)
                .status(UserStatus.ACTIVE)
                .build();
    }

    @Test
    void savesAndFindsByLoginIdentifier() {
        UserAccountEntity saved = userAccounts.save(account("findme@example.com"));
        assertNotNull(saved.getId(), "JPA should assign a UUID id");

        Optional<UserAccountEntity> found = userAccounts.findByLoginIdentifier("findme@example.com");
        assertTrue(found.isPresent());
        assertEquals(saved.getId(), found.get().getId());

        assertTrue(userAccounts.existsByLoginIdentifier("findme@example.com"));
        assertFalse(userAccounts.existsByLoginIdentifier("nobody@example.com"));
    }

    @Test
    void rejectsDuplicateLoginIdentifier() {
        userAccounts.saveAndFlush(account("dup@example.com"));

        assertThatThrownBy(() -> userAccounts.saveAndFlush(account("dup@example.com")))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}
