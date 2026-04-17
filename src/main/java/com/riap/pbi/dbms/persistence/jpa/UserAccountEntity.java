package com.riap.pbi.dbms.persistence.jpa;

import com.riap.pbi.dbms.domain.UserAccountRole;
import com.riap.pbi.dbms.domain.UserAccountStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "user_accounts", uniqueConstraints = {
        @UniqueConstraint(name = "uk_user_accounts_login_identifier", columnNames = "login_identifier")
})
public class UserAccountEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "login_identifier", nullable = false, length = 100)
    private String loginIdentifier;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private UserAccountRole role;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private UserAccountStatus status;

    protected UserAccountEntity() {
    }

    public UserAccountEntity(Long id, String loginIdentifier, String passwordHash, UserAccountRole role, UserAccountStatus status) {
        this.id = id;
        this.loginIdentifier = loginIdentifier;
        this.passwordHash = passwordHash;
        this.role = role;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public String getLoginIdentifier() {
        return loginIdentifier;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public UserAccountRole getRole() {
        return role;
    }

    public UserAccountStatus getStatus() {
        return status;
    }
}