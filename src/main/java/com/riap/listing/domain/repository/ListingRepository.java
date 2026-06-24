package com.riap.listing.domain.repository;

import com.riap.listing.domain.model.ListingEntity;
import com.riap.listing.domain.model.ListingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

@Repository
public interface ListingRepository extends JpaRepository<ListingEntity, UUID>, JpaSpecificationExecutor<ListingEntity> {
    List<ListingEntity> findByStatus(ListingStatus status);
    List<ListingEntity> findByLandlordId(String landlordId);
}
