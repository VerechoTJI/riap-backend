package com.riap.listing.application.service;

import com.riap.listing.domain.model.ListingEntity;
import com.riap.listing.domain.model.ListingStatus;
import com.riap.listing.domain.repository.ListingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ListingService {

    private final ListingRepository listingRepository;

    @Transactional
    public ListingEntity publishListing(ListingEntity listing) {
        // Basic validation for mandatory fields (AC-LMS-01)
        if (listing.getFeeDisclosure() == null || 
            listing.getFeeDisclosure().getRent() == null ||
            listing.getFeeDisclosure().getDeposit() == null ||
            listing.getFeeDisclosure().getManagementFee() == null ||
            listing.getFeeDisclosure().getWaterElectricityRules() == null) {
            throw new IllegalArgumentException("所有費用欄位均為必填");
        }

        listing.setStatus(ListingStatus.PENDING);
        return listingRepository.save(listing);
    }

    @Transactional
    public ListingEntity updateListing(UUID id, ListingEntity updated) {
        ListingEntity existing = listingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("房源不存在"));

        // Basic validation for mandatory fields
        if (updated.getFeeDisclosure() == null || 
            updated.getFeeDisclosure().getRent() == null ||
            updated.getFeeDisclosure().getDeposit() == null ||
            updated.getFeeDisclosure().getManagementFee() == null ||
            updated.getFeeDisclosure().getWaterElectricityRules() == null) {
            throw new IllegalArgumentException("所有費用欄位均為必填");
        }

        existing.setTitle(updated.getTitle());
        existing.setDescription(updated.getDescription());
        existing.setArea(updated.getArea());
        existing.setFloor(updated.getFloor());
        existing.setLayout(updated.getLayout());
        existing.setPropertyType(updated.getPropertyType());
        existing.setFeeDisclosure(updated.getFeeDisclosure());
        existing.setCity(updated.getCity());
        existing.setDistrict(updated.getDistrict());
        existing.setAddress(updated.getAddress());
        existing.setTotalFloors(updated.getTotalFloors());
        existing.setHasInternet(updated.getHasInternet());
        existing.setHasFurniture(updated.getHasFurniture());
        existing.setHasAC(updated.getHasAC());
        existing.setPetFriendly(updated.getPetFriendly());
        existing.setHasParking(updated.getHasParking());
        existing.setAvailableFrom(updated.getAvailableFrom());
        existing.setImageUrl(updated.getImageUrl());
        
        // When updated, send to pending again for review
        existing.setStatus(ListingStatus.PENDING);
        existing.setReturnReason(null);

        return listingRepository.save(existing);
    }

    @Transactional
    public ListingEntity reviewListing(UUID listingId, ListingStatus newStatus, String reason) {
        ListingEntity listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new IllegalArgumentException("房源不存在"));

        if (newStatus == ListingStatus.RETURNED && (reason == null || reason.isBlank())) {
            throw new IllegalArgumentException("退回房源必須填寫原因");
        }

        listing.setStatus(newStatus);
        listing.setReturnReason(reason);
        return listingRepository.save(listing);
    }

    @Transactional
    public ListingEntity unpublishListing(UUID listingId) {
        ListingEntity listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new IllegalArgumentException("房源不存在"));
        
        listing.setStatus(ListingStatus.PRIVATE);
        return listingRepository.save(listing);
    }

    @Transactional
    public ListingEntity resubmitListing(UUID listingId) {
        ListingEntity listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new IllegalArgumentException("房源不存在"));
        
        // Only PRIVATE or RETURNED listings can be resubmitted
        if (listing.getStatus() == ListingStatus.PUBLISHED || listing.getStatus() == ListingStatus.PENDING) {
            throw new IllegalStateException("目前狀態無法重新送審");
        }

        listing.setStatus(ListingStatus.PENDING);
        listing.setReturnReason(null); // Clear previous return reason
        return listingRepository.save(listing);
    }

    @Transactional
    public void unpublishAllByLandlord(UUID landlordId) {
        List<ListingEntity> listings = listingRepository.findByLandlordId(landlordId);
        listings.stream()
                .filter(l -> l.getStatus() == ListingStatus.PUBLISHED)
                .forEach(l -> l.setStatus(ListingStatus.PRIVATE));
        listingRepository.saveAll(listings);
    }

    public List<ListingEntity> getPendingListings() {
        return listingRepository.findByStatus(ListingStatus.PENDING);
    }

    public List<ListingEntity> getPublishedListings() {
        return listingRepository.findByStatus(ListingStatus.PUBLISHED);
    }

    public List<ListingEntity> getListingsByLandlordId(UUID landlordId) {
        return listingRepository.findByLandlordId(landlordId);
    }
}
