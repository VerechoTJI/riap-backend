package com.riap.pbi.dbms.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class UserAccountTest {

    @Test
    void createRejectsBlankFields() {
        assertThrows(IllegalArgumentException.class, () -> UserAccount.create("", "hash", UserAccountRole.TENANT, UserAccountStatus.ACTIVE));
        assertThrows(IllegalArgumentException.class, () -> UserAccount.create("user@example.com", " ", UserAccountRole.TENANT, UserAccountStatus.ACTIVE));
    }

    @Test
    void rehydratePreservesAccountData() {
        UserAccount account = UserAccount.rehydrate(10L, "user@example.com", "hash", UserAccountRole.LANDLORD, UserAccountStatus.LOCKED);

        assertEquals(10L, account.getId());
        assertEquals("user@example.com", account.getLoginIdentifier());
        assertEquals("hash", account.getPasswordHash());
        assertEquals(UserAccountRole.LANDLORD, account.getRole());
        assertEquals(UserAccountStatus.LOCKED, account.getStatus());
    }
}