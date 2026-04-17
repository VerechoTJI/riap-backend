package com.riap.pbi.dbms.persistence.jpa;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.riap.pbi.dbms.domain.UserAccount;
import com.riap.pbi.dbms.domain.UserAccountRole;
import com.riap.pbi.dbms.domain.UserAccountStatus;
import org.junit.jupiter.api.Test;

class UserAccountMapperTest {

    @Test
    void mapsBetweenDomainAndEntity() {
        UserAccount account = UserAccount.rehydrate(7L, "tenant@example.com", "hash", UserAccountRole.TENANT, UserAccountStatus.ACTIVE);

        UserAccountEntity entity = UserAccountMapper.toEntity(account);
        UserAccount mappedBack = UserAccountMapper.toDomain(entity);

        assertEquals(account.getId(), mappedBack.getId());
        assertEquals(account.getLoginIdentifier(), mappedBack.getLoginIdentifier());
        assertEquals(account.getPasswordHash(), mappedBack.getPasswordHash());
        assertEquals(account.getRole(), mappedBack.getRole());
        assertEquals(account.getStatus(), mappedBack.getStatus());
    }
}