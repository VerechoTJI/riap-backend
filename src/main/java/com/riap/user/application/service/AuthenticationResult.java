package com.riap.user.application.service;

import com.riap.user.domain.model.UserRole;

import java.util.UUID;

/**
 * Outcome of an authentication attempt (response side of SDD 5.2.1 {@code login}).
 * On success it carries the user id and role for role-based routing (UAS-F-03).
 * Unknown account and wrong password both map to {@link Outcome#INVALID_CREDENTIALS}
 * so the response does not reveal which accounts exist.
 */
public record AuthenticationResult(Outcome outcome, UUID userId, UserRole role) {

    public enum Outcome {
        SUCCESS,
        INVALID_CREDENTIALS,
        ACCOUNT_LOCKED,
        ACCOUNT_DISABLED
    }

    public boolean isSuccess() {
        return outcome == Outcome.SUCCESS;
    }

    public static AuthenticationResult success(UUID userId, UserRole role) {
        return new AuthenticationResult(Outcome.SUCCESS, userId, role);
    }

    public static AuthenticationResult failure(Outcome outcome) {
        return new AuthenticationResult(outcome, null, null);
    }
}
