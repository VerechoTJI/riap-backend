package com.riap.pbi.dbms.persistence;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public final class JPAEntityManagerFactoryProvider {

    private JPAEntityManagerFactoryProvider() {
    }

    public static EntityManagerFactory create(String persistenceUnitName) {
        return Persistence.createEntityManagerFactory(persistenceUnitName);
    }

    public static void close(EntityManagerFactory emf) {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}
