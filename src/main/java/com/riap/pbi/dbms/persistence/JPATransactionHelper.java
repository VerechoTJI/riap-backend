package com.riap.pbi.dbms.persistence;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.util.function.Function;

public final class JPATransactionHelper {

    private final EntityManagerFactory emf;

    public JPATransactionHelper(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public <T> T inTransaction(Function<EntityManager, T> callback) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            T result = callback.apply(em);
            em.getTransaction().commit();
            return result;
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public void inTransactionVoid(java.util.function.Consumer<EntityManager> callback) {
        inTransaction(em -> {
            callback.accept(em);
            return null;
        });
    }
}
