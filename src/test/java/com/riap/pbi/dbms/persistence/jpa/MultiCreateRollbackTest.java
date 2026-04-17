package com.riap.pbi.dbms.persistence.jpa;

import com.riap.pbi.dbms.persistence.JPAEntityManagerFactoryProvider;
import com.riap.pbi.dbms.persistence.JPATransactionHelper;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

public class MultiCreateRollbackTest {

    private static EntityManagerFactory emf;
    private static JPATransactionHelper txHelper;

    @BeforeAll
    public static void setUp() {
        emf = JPAEntityManagerFactoryProvider.create("test-pu");
        txHelper = new JPATransactionHelper(emf);
    }

    @AfterAll
    public static void tearDown() {
        JPAEntityManagerFactoryProvider.close(emf);
    }

    @Test
    public void multipleCreatesRollbackTogether() {
        try {
            txHelper.inTransaction(em -> {
                UserAccountEntity u1 = new UserAccountEntity(null, "multi1@example.com", "h1", com.riap.pbi.dbms.domain.UserAccountRole.TENANT, com.riap.pbi.dbms.domain.UserAccountStatus.ACTIVE);
                em.persist(u1);

                UserAccountEntity u2 = new UserAccountEntity(null, "multi2@example.com", "h2", com.riap.pbi.dbms.domain.UserAccountRole.TENANT, com.riap.pbi.dbms.domain.UserAccountStatus.ACTIVE);
                em.persist(u2);

                // force failure so both persists should be rolled back
                throw new RuntimeException("force rollback");
            });
        } catch (RuntimeException ignored) {
        }

        var em = emf.createEntityManager();
        try {
            TypedQuery<UserAccountEntity> q = em.createQuery("SELECT u FROM UserAccountEntity u WHERE u.loginIdentifier LIKE :p", UserAccountEntity.class);
            q.setParameter("p", "multi%" );
            List<UserAccountEntity> res = q.getResultList();
            Assertions.assertTrue(res.isEmpty(), "No created users should exist after rollback");
        } finally {
            em.close();
        }
    }
}
