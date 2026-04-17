package com.riap.pbi.dbms.persistence.jpa;

import com.riap.pbi.dbms.domain.UserAccount;
import com.riap.pbi.dbms.domain.UserAccountRole;
import com.riap.pbi.dbms.domain.UserAccountStatus;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Optional;

class UserAccountRepositoryIT {

    private static EntityManagerFactory emf;

    @BeforeAll
    static void setup() {
        emf = Persistence.createEntityManagerFactory("test-pu");
    }

    @AfterAll
    static void tearDown() {
        if (emf != null) emf.close();
    }

    @Test
    void saveFindAndDelete() {
        UserAccountJpaAdapter adapter = new UserAccountJpaAdapter(emf);

        UserAccount newAccount = UserAccount.create("ituser@example.com", "hash", UserAccountRole.TENANT, UserAccountStatus.ACTIVE);
        UserAccount saved = adapter.save(newAccount);

        Assertions.assertNotNull(saved.getId(), "Saved account should have an id");

        Optional<UserAccount> found = adapter.findByLoginIdentifier("ituser@example.com");
        Assertions.assertTrue(found.isPresent());
        Assertions.assertEquals("ituser@example.com", found.get().getLoginIdentifier());

        adapter.deleteById(saved.getId());
        Assertions.assertTrue(adapter.findById(saved.getId()).isEmpty());
    }
}
