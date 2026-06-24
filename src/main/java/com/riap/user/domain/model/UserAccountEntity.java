package com.riap.user.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * A user account for the User Authentication Subsystem (UAS). The login identifier
 * is unique; the password is stored only as a one-way hash (UAS-N-09). Other
 * aggregates reference a user by this {@code id} (e.g. {@code landlordId} on a
 * listing), matching the project convention of UUID references across boundaries.
 */
@Entity
@Table(
        name = "user_accounts",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_user_accounts_login_identifier",
                columnNames = "login_identifier"))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAccountEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "login_identifier", nullable = false)
    private String loginIdentifier;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status;
}
