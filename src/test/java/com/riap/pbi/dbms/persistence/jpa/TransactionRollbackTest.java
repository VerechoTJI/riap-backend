package com.riap.pbi.dbms.persistence.jpa;

import com.riap.pbi.dbms.domain.UserAccountRole;
import com.riap.pbi.dbms.domain.UserAccountStatus;
import com.riap.pbi.dbms.persistence.JPAEntityManagerFactoryProvider;
import com.riap.pbi.dbms.persistence.JPATransactionHelper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

public class TransactionRollbackTest {

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
    public void rollbackOnRuntimeExceptionLeavesNoPartialData() {
        // attempt a transactional operation that fails in the middle
        try {
            txHelper.inTransaction(em -> {
                UserAccountEntity u = new UserAccountEntity(null, "txn_user", "hash", UserAccountRole.TENANT, UserAccountStatus.ACTIVE);
                em.persist(u);
                // force a runtime failure after persist to trigger rollback
                throw new RuntimeException("forced failure to test rollback");
            });
        } catch (RuntimeException ignored) {
        }

        // ensure that the persisted entity was rolled back and is not present
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<UserAccountEntity> q = em.createQuery(
                    "SELECT u FROM UserAccountEntity u WHERE u.loginIdentifier = :login",
                    UserAccountEntity.class);
            q.setParameter("login", "txn_user");
            List<UserAccountEntity> results = q.getResultList();
            Assertions.assertTrue(results.isEmpty(), "No user should be present after rollback");
        } finally {
            em.close();
        }
    }
}
