package com.riap.pbi.dbms.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ListingTest {

    @Test
    void builderCreatesNewListingWithRequiredFields() {
        Listing listing = Listing.builder()
                .title("Studio in Taipei")
                .rentCents(32800L)
                .landlordId(1L)
                .city("台北市")
                .propertyType(PropertyType.STUDIO)
                .sizePing(9)
                .build();

        assertNull(listing.getId());
        assertEquals("Studio in Taipei", listing.getTitle());
        assertEquals(32800L, listing.getRentCents());
        assertEquals(1L, listing.getLandlordId());
        assertEquals("台北市", listing.getCity());
        assertEquals(PropertyType.STUDIO, listing.getPropertyType());
        assertEquals(9, listing.getSizePing());
        assertEquals(ListingStatus.PENDING, listing.getStatus());
    }

    @Test
    void builderCreatesListingWithAllOptionalFields() {
        LocalDate available = LocalDate.of(2026, 6, 1);
        LocalDateTime posted = LocalDateTime.of(2026, 4, 14, 10, 0);

        Listing listing = Listing.builder()
                .title("River View Apartment")
                .rentCents(16800L)
                .landlordId(2L)
                .city("新北市")
                .district("板橋區")
                .address("新北市板橋區文化路一段 120 號")
                .propertyType(PropertyType.APARTMENT)
                .sizePing(21)
                .floor(12)
                .totalFloors(15)
                .depositCents(33600L)
                .managementFeeCents(1200L)
                .description("室內採光好，適合小家庭")
                .hasInternet(true)
                .hasFurniture(false)
                .hasAC(true)
                .petFriendly(true)
                .hasParking(false)
                .availableFrom(available)
                .postedAt(posted)
                .imageUrl("https://example.com/img.jpg")
                .build();

        assertEquals("新北市", listing.getCity());
        assertEquals("板橋區", listing.getDistrict());
        assertEquals("新北市板橋區文化路一段 120 號", listing.getAddress());
        assertEquals(21, listing.getSizePing());
        assertEquals(12, listing.getFloor());
        assertEquals(15, listing.getTotalFloors());
        assertEquals(33600L, listing.getDepositCents());
        assertEquals(1200L, listing.getManagementFeeCents());
        assertTrue(listing.isHasInternet());
        assertFalse(listing.isHasFurniture());
        assertTrue(listing.isHasAC());
        assertTrue(listing.isPetFriendly());
        assertFalse(listing.isHasParking());
        assertEquals(available, listing.getAvailableFrom());
        assertEquals(posted, listing.getPostedAt());
        assertEquals("https://example.com/img.jpg", listing.getImageUrl());
    }

    @Test
    void builderWithExplicitStatusPreservesIt() {
        Listing listing = Listing.builder()
                .id(42L)
                .title("Restored Listing")
                .rentCents(20000L)
                .landlordId(3L)
                .city("台中市")
                .propertyType(PropertyType.HOUSE)
                .sizePing(30)
                .status(ListingStatus.AVAILABLE)
                .build();

        assertEquals(42L, listing.getId());
        assertEquals(ListingStatus.AVAILABLE, listing.getStatus());
    }

    @Test
    void builderRejectsNullTitle() {
        assertThrows(NullPointerException.class, () ->
                Listing.builder()
                        .rentCents(25000L)
                        .landlordId(1L)
                        .city("台北市")
                        .propertyType(PropertyType.STUDIO)
                        .sizePing(10)
                        .build()
        );
    }

    @Test
    void builderRejectsNullLandlordId() {
        assertThrows(NullPointerException.class, () ->
                Listing.builder()
                        .title("Test")
                        .rentCents(25000L)
                        .city("台北市")
                        .propertyType(PropertyType.STUDIO)
                        .sizePing(10)
                        .build()
        );
    }

    @Test
    void builderRejectsNullCity() {
        assertThrows(NullPointerException.class, () ->
                Listing.builder()
                        .title("Test")
                        .rentCents(25000L)
                        .landlordId(1L)
                        .propertyType(PropertyType.STUDIO)
                        .sizePing(10)
                        .build()
        );
    }

    @Test
    void builderRejectsNullPropertyType() {
        assertThrows(NullPointerException.class, () ->
                Listing.builder()
                        .title("Test")
                        .rentCents(25000L)
                        .landlordId(1L)
                        .city("台北市")
                        .sizePing(10)
                        .build()
        );
    }
}
