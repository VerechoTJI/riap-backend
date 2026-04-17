package com.riap.pbi.dbms.domain;

import java.util.Objects;

public final class UserAccount {

    private final Long id;
    private final String loginIdentifier;
    private final String passwordHash;
    private final UserAccountRole role;
    private final UserAccountStatus status;

    private UserAccount(Long id, String loginIdentifier, String passwordHash, UserAccountRole role, UserAccountStatus status) {
        this.id = id;
        this.loginIdentifier = requireText(loginIdentifier, "loginIdentifier");
        this.passwordHash = requireText(passwordHash, "passwordHash");
        this.role = Objects.requireNonNull(role, "role must not be null");
        this.status = Objects.requireNonNull(status, "status must not be null");
    }

    public static UserAccount create(String loginIdentifier, String passwordHash, UserAccountRole role, UserAccountStatus status) {
        return new UserAccount(null, loginIdentifier, passwordHash, role, status);
    }

    public static UserAccount rehydrate(Long id, String loginIdentifier, String passwordHash, UserAccountRole role, UserAccountStatus status) {
        return new UserAccount(id, loginIdentifier, passwordHash, role, status);
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

    private static String requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value;
    }
}