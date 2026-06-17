package com.riap.pbi.dbms.persistence.jpa;

import com.riap.pbi.dbms.domain.Listing;
import com.riap.pbi.dbms.domain.ListingFilter;
import com.riap.pbi.dbms.domain.ListingStatus;
import com.riap.pbi.dbms.domain.SortOrder;
import com.riap.pbi.dbms.repository.ListingRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;

import java.util.ArrayList;
import java.util.List;
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

    @Override
    public List<Listing> search(ListingFilter filter) {
        EntityManager em = emf.createEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<ListingEntity> cq = cb.createQuery(ListingEntity.class);
            Root<ListingEntity> root = cq.from(ListingEntity.class);

            List<Predicate> predicates = new ArrayList<>();

            // Only AVAILABLE listings are publicly searchable
            predicates.add(cb.equal(root.get("status"), ListingStatus.AVAILABLE));

            if (filter.city() != null) {
                predicates.add(cb.equal(root.get("city"), filter.city()));
            }

            if (filter.propertyType() != null) {
                predicates.add(cb.equal(root.get("propertyType"), filter.propertyType()));
            }

            if (filter.minRentCents() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("rentCents"), filter.minRentCents()));
            }

            if (filter.maxRentCents() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("rentCents"), filter.maxRentCents()));
            }

            if (filter.hasInternet() != null) {
                predicates.add(cb.equal(root.get("hasInternet"), filter.hasInternet()));
            }

            if (filter.hasFurniture() != null) {
                predicates.add(cb.equal(root.get("hasFurniture"), filter.hasFurniture()));
            }

            if (filter.hasAC() != null) {
                predicates.add(cb.equal(root.get("hasAC"), filter.hasAC()));
            }

            if (filter.petFriendly() != null) {
                predicates.add(cb.equal(root.get("petFriendly"), filter.petFriendly()));
            }

            if (filter.hasParking() != null) {
                predicates.add(cb.equal(root.get("hasParking"), filter.hasParking()));
            }

            if (filter.keyword() != null && !filter.keyword().isBlank()) {
                String pattern = "%" + filter.keyword().trim() + "%";
                Predicate titleLike = cb.like(root.get("title"), pattern);
                Predicate districtLike = cb.like(cb.coalesce(root.get("district"), ""), pattern);
                predicates.add(cb.or(titleLike, districtLike));
            }

            cq.where(predicates.toArray(new Predicate[0]));
            cq.orderBy(toOrder(cb, root, filter.sortOrder()));

            TypedQuery<ListingEntity> query = em.createQuery(cq);
            query.setFirstResult(filter.page() * filter.size());
            query.setMaxResults(filter.size());

            return query.getResultList().stream()
                    .map(ListingMapper::toDomain)
                    .toList();
        } finally {
            em.close();
        }
    }

    private Order toOrder(CriteriaBuilder cb, Root<ListingEntity> root, SortOrder sortOrder) {
        return switch (sortOrder) {
            case RENT_ASC      -> cb.asc(root.get("rentCents"));
            case RENT_DESC     -> cb.desc(root.get("rentCents"));
            case POSTED_AT_ASC -> cb.asc(root.get("postedAt"));
            case POSTED_AT_DESC -> cb.desc(root.get("postedAt"));
        };
    }
}
