package com.riap.pbi.dbms.persistence.jpa;

import com.riap.pbi.dbms.domain.UserAccount;
import com.riap.pbi.dbms.repository.UserAccountRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;

public class UserAccountJpaAdapter implements UserAccountRepository {

    private final EntityManagerFactory emf;

    public UserAccountJpaAdapter(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public Optional<UserAccount> findById(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            UserAccountEntity entity = em.find(UserAccountEntity.class, id);
            return entity == null ? Optional.empty() : Optional.of(UserAccountMapper.toDomain(entity));
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<UserAccount> findByLoginIdentifier(String loginIdentifier) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<UserAccountEntity> q = em.createQuery("SELECT u FROM UserAccountEntity u WHERE u.loginIdentifier = :login", UserAccountEntity.class);
            q.setParameter("login", loginIdentifier);
            List<UserAccountEntity> list = q.getResultList();
            if (list.isEmpty()) return Optional.empty();
            return Optional.of(UserAccountMapper.toDomain(list.get(0)));
        } finally {
            em.close();
        }
    }

    @Override
    public UserAccount save(UserAccount account) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            UserAccountEntity entity = UserAccountMapper.toEntity(account);
            if (account.getId() == null) {
                em.persist(entity);
                em.flush();
                em.getTransaction().commit();
                return UserAccountMapper.toDomain(entity);
            } else {
                UserAccountEntity merged = em.merge(entity);
                em.getTransaction().commit();
                return UserAccountMapper.toDomain(merged);
            }
        } finally {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            em.close();
        }
    }

    @Override
    public void deleteById(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            UserAccountEntity entity = em.find(UserAccountEntity.class, id);
            if (entity != null) {
                em.remove(entity);
            }
            em.getTransaction().commit();
        } finally {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            em.close();
        }
    }
}
