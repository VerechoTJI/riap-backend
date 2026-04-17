package com.riap.pbi.dbms.persistence.jpa;

import com.riap.pbi.dbms.domain.Listing;
import com.riap.pbi.dbms.domain.ListingStatus;
import com.riap.pbi.dbms.repository.ListingRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;

import java.util.Optional;

public class ListingJpaAdapter implements ListingRepository {

    private final EntityManagerFactory emf;

    public ListingJpaAdapter(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public Listing save(Listing listing) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            UserAccountEntity landlord = em.find(UserAccountEntity.class, listing.getLandlordId());
            if (landlord == null) throw new IllegalArgumentException("landlord not found");

            ListingEntity entity = ListingMapper.toEntity(listing, landlord);
            if (entity.getId() == null) {
                em.persist(entity);
            } else {
                entity = em.merge(entity);
            }
            em.getTransaction().commit();
            return ListingMapper.toDomain(entity);
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<Listing> findById(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            ListingEntity e = em.find(ListingEntity.class, id);
            return Optional.ofNullable(ListingMapper.toDomain(e));
        } finally {
            em.close();
        }
    }

    @Override
    public void deleteById(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            ListingEntity e = em.find(ListingEntity.class, id);
            if (e != null) em.remove(e);
            em.getTransaction().commit();
        } finally {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            em.close();
        }
    }
}
