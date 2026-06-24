package com.riap.listing.application.service;

import com.riap.listing.domain.model.FeeDisclosure;
import com.riap.listing.domain.model.ListingEntity;
import com.riap.listing.domain.model.ListingStatus;
import com.riap.listing.domain.repository.ListingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ListingServiceTest {

    @Mock
    private ListingRepository listingRepository;

    @InjectMocks
    private ListingService listingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void publishListing_ShouldSucceed_WhenAllFieldsPresent() {
        ListingEntity listing = ListingEntity.builder()
                .title("Test Listing")
                .feeDisclosure(FeeDisclosure.builder()
                        .rent(new BigDecimal("10000"))
                        .deposit(new BigDecimal("20000"))
                        .managementFee(new BigDecimal("500"))
                        .waterElectricityRules("Standard")
                        .build())
                .build();

        when(listingRepository.save(any())).thenReturn(listing);

        ListingEntity saved = listingService.publishListing(listing);

        assertNotNull(saved);
        assertEquals(ListingStatus.PENDING, saved.getStatus());
        verify(listingRepository, times(1)).save(listing);
    }

    @Test
    void publishListing_ShouldThrowException_WhenMandatoryFieldsMissing() {
        ListingEntity listing = ListingEntity.builder()
                .title("Test Listing")
                .feeDisclosure(FeeDisclosure.builder()
                        .rent(new BigDecimal("10000"))
                        // deposit missing
                        .build())
                .build();

        assertThrows(IllegalArgumentException.class, () -> listingService.publishListing(listing));
    }

    @Test
    void reviewListing_ShouldUpdateStatusAndReason() {
        UUID id = UUID.randomUUID();
        ListingEntity listing = ListingEntity.builder().id(id).status(ListingStatus.PENDING).build();

        when(listingRepository.findById(id)).thenReturn(Optional.of(listing));
        when(listingRepository.save(any())).thenReturn(listing);

        ListingEntity reviewed = listingService.reviewListing(id, ListingStatus.RETURNED, "Missing photos");

        assertEquals(ListingStatus.RETURNED, reviewed.getStatus());
        assertEquals("Missing photos", reviewed.getReturnReason());
    }

    @Test
    void unpublishListing_ShouldSetStatusToPrivate() {
        UUID id = UUID.randomUUID();
        ListingEntity listing = ListingEntity.builder().id(id).status(ListingStatus.PUBLISHED).build();

        when(listingRepository.findById(id)).thenReturn(Optional.of(listing));
        when(listingRepository.save(any())).thenReturn(listing);

        ListingEntity unpublished = listingService.unpublishListing(id);

        assertEquals(ListingStatus.PRIVATE, unpublished.getStatus());
    }

    @Test
    void resubmitListing_ShouldChangeToPending() {
        UUID id = UUID.randomUUID();
        ListingEntity listing = ListingEntity.builder().id(id).status(ListingStatus.PRIVATE).build();

        when(listingRepository.findById(id)).thenReturn(Optional.of(listing));
        when(listingRepository.save(any())).thenReturn(listing);

        ListingEntity resubmitted = listingService.resubmitListing(id);

        assertEquals(ListingStatus.PENDING, resubmitted.getStatus());
        assertNull(resubmitted.getReturnReason());
    }

    @Test
    void unpublishAllByLandlord_ShouldSetStatusToPrivate() {
        String landlordId = UUID.randomUUID().toString();
        ListingEntity l1 = ListingEntity.builder().status(ListingStatus.PUBLISHED).build();
        ListingEntity l2 = ListingEntity.builder().status(ListingStatus.PENDING).build();
        
        when(listingRepository.findByLandlordId(landlordId)).thenReturn(java.util.List.of(l1, l2));

        listingService.unpublishAllByLandlord(landlordId);

        assertEquals(ListingStatus.PRIVATE, l1.getStatus());
        assertEquals(ListingStatus.PENDING, l2.getStatus()); // Should remain pending
        verify(listingRepository, times(1)).saveAll(any());
    }
}
