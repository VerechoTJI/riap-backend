package com.riap.dbms;

import com.riap.user.domain.model.UserAccountEntity;
import com.riap.user.domain.model.UserRole;
import com.riap.user.domain.model.UserStatus;
import com.riap.user.domain.repository.UserAccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Referential-integrity tests for the listings -> user_accounts foreign key, run
 * against a real PostgreSQL (the FK is applied by schema-postgres.sql under the
 * {@code postgres} profile; H2 unit tests do not carry it).
 *
 * <p>Gated on {@code DB_PASSWORD} so it only runs when a PostgreSQL instance is
 * configured (e.g. locally), and is skipped — not failed — elsewhere. Run with:
 * {@code DB_PASSWORD=... mvn test -Dspring.profiles.active=...} (profile set here).
 *
 * <ul>
 *   <li>STD DBMS-TC02 (DBMS-F-03): a listing referencing a non-existent landlord is rejected.</li>
 *   <li>STD DBMS-TC03 (DBMS-N-11): deleting a landlord that still owns a listing is rejected.</li>
 * </ul>
 */
@SpringBootTest
@ActiveProfiles("postgres")
@EnabledIfEnvironmentVariable(named = "DB_PASSWORD", matches = ".+")
class DbmsForeignKeyPostgresTest {

    @Autowired
    private JdbcTemplate jdbc;

    @Autowired
    private UserAccountRepository users;

    @Test
    void listingWithNonExistentLandlordIsRejected() {
        assertThrows(DataIntegrityViolationException.class, () ->
                jdbc.update("INSERT INTO listings (id, landlord_id) VALUES (?, ?)",
                        UUID.randomUUID(), UUID.randomUUID()));
    }

    @Test
    void deletingLandlordWithListingsIsRejected() {
        UserAccountEntity landlord = users.save(UserAccountEntity.builder()
                .loginIdentifier("fk-it-" + UUID.randomUUID() + "@example.com")
                .passwordHash("hash")
                .role(UserRole.LANDLORD)
                .status(UserStatus.ACTIVE)
                .build());
        UUID listingId = UUID.randomUUID();
        jdbc.update("INSERT INTO listings (id, landlord_id) VALUES (?, ?)", listingId, landlord.getId());

        try {
            assertThrows(DataIntegrityViolationException.class, () ->
                    jdbc.update("DELETE FROM user_accounts WHERE id = ?", landlord.getId()));
        } finally {
            // Clean up the rows this test created.
            jdbc.update("DELETE FROM listings WHERE id = ?", listingId);
            users.deleteById(landlord.getId());
        }
    }
}
