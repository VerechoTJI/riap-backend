package com.riap.pbi.service;

import com.riap.listing.domain.model.ListingEntity;
import com.riap.listing.domain.model.ListingStatus;
import com.riap.pbi.dbms.domain.ListingFilter;
import com.riap.pbi.dbms.domain.SortOrder;
import com.riap.listing.domain.repository.ListingRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.criteria.Predicate;
import java.math.BigDecimal;

@Service
public class ListingSearchService {

    private final ListingRepository listingRepository;

    public ListingSearchService(ListingRepository listingRepository) {
        this.listingRepository = listingRepository;
    }

    public List<ListingEntity> search(ListingFilter filter) {
        ListingFilter effective = filter != null ? filter : ListingFilter.empty();
        
        Specification<ListingEntity> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // Only published listings should be searchable by tenants
            predicates.add(cb.equal(root.get("status"), ListingStatus.PUBLISHED));

            if (effective.keyword() != null && !effective.keyword().trim().isEmpty()) {
                String pattern = "%" + effective.keyword().toLowerCase() + "%";
                Predicate titleMatch = cb.like(cb.lower(root.get("title")), pattern);
                Predicate descMatch = cb.like(cb.lower(root.get("description")), pattern);
                predicates.add(cb.or(titleMatch, descMatch));
            }

            if (effective.city() != null && !effective.city().trim().isEmpty()) {
                predicates.add(cb.equal(root.get("city"), effective.city()));
            }

            if (effective.propertyType() != null) {
                predicates.add(cb.equal(root.get("propertyType"), effective.propertyType()));
            }

            if (effective.minRentCents() != null) {
                BigDecimal minRent = new BigDecimal(effective.minRentCents()).divide(new BigDecimal(100));
                predicates.add(cb.greaterThanOrEqualTo(root.get("feeDisclosure").get("rent"), minRent));
            }

            if (effective.maxRentCents() != null) {
                BigDecimal maxRent = new BigDecimal(effective.maxRentCents()).divide(new BigDecimal(100));
                predicates.add(cb.lessThanOrEqualTo(root.get("feeDisclosure").get("rent"), maxRent));
            }

            if (Boolean.TRUE.equals(effective.hasInternet())) predicates.add(cb.isTrue(root.get("hasInternet")));
            if (Boolean.TRUE.equals(effective.hasFurniture())) predicates.add(cb.isTrue(root.get("hasFurniture")));
            if (Boolean.TRUE.equals(effective.hasAC())) predicates.add(cb.isTrue(root.get("hasAC")));
            if (Boolean.TRUE.equals(effective.petFriendly())) predicates.add(cb.isTrue(root.get("petFriendly")));
            if (Boolean.TRUE.equals(effective.hasParking())) predicates.add(cb.isTrue(root.get("hasParking")));

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Sort sort = Sort.by(Sort.Direction.DESC, "postedAt");
        if (effective.sortOrder() == SortOrder.RENT_ASC) {
            sort = Sort.by(Sort.Direction.ASC, "feeDisclosure.rent");
        } else if (effective.sortOrder() == SortOrder.RENT_DESC) {
            sort = Sort.by(Sort.Direction.DESC, "feeDisclosure.rent");
        } else if (effective.sortOrder() == SortOrder.POSTED_AT_ASC) {
            sort = Sort.by(Sort.Direction.ASC, "postedAt");
        }

        PageRequest pageRequest = PageRequest.of(effective.page(), effective.size(), sort);
        Page<ListingEntity> pageResult = listingRepository.findAll(spec, pageRequest);
        return pageResult.getContent();
    }
}
