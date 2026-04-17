package com.riap.pbi.dbms.repository;

import com.riap.pbi.dbms.domain.UserAccount;

import java.util.Optional;

public interface UserAccountRepository {

    Optional<UserAccount> findById(Long id);

    Optional<UserAccount> findByLoginIdentifier(String loginIdentifier);

    UserAccount save(UserAccount account);

    void deleteById(Long id);
}