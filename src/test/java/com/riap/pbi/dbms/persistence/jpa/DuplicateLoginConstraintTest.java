package com.riap.pbi.dbms.persistence.jpa;

import com.riap.pbi.dbms.domain.UserAccountRole;
import com.riap.pbi.dbms.domain.UserAccountStatus;
import com.riap.pbi.dbms.persistence.JPAEntityManagerFactoryProvider;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import jakarta.persistence.PersistenceException;

class DuplicateLoginConstraintTest {

    private static EntityManagerFactory emf;

    @BeforeAll
    static void setup() {
        emf = JPAEntityManagerFactoryProvider.create("test-pu");
    }

    @AfterAll
    static void tearDown() {
        JPAEntityManagerFactoryProvider.close(emf);
    }

    @Test
    void uniqueLoginIdentifierConstraint() {
        UserAccountJpaAdapter adapter = new UserAccountJpaAdapter(emf);

        var a1 = com.riap.pbi.dbms.domain.UserAccount.create("dup@example.com", "hash", UserAccountRole.TENANT, UserAccountStatus.ACTIVE);
        adapter.save(a1);

        var a2 = com.riap.pbi.dbms.domain.UserAccount.create("dup@example.com", "hash2", UserAccountRole.TENANT, UserAccountStatus.ACTIVE);

        Assertions.assertThrows(PersistenceException.class, () -> adapter.save(a2));
    }
}
