package com.riap.pbi.dbms.persistence.jpa;

import com.riap.pbi.dbms.domain.UserAccount;

public final class UserAccountMapper {

    private UserAccountMapper() {
    }

    public static UserAccountEntity toEntity(UserAccount account) {
        return new UserAccountEntity(
                account.getId(),
                account.getLoginIdentifier(),
                account.getPasswordHash(),
                account.getRole(),
                account.getStatus());
    }

    public static UserAccount toDomain(UserAccountEntity entity) {
        return UserAccount.rehydrate(
                entity.getId(),
                entity.getLoginIdentifier(),
                entity.getPasswordHash(),
                entity.getRole(),
                entity.getStatus());
    }
}