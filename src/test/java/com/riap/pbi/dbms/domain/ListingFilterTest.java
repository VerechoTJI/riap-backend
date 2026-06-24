package com.riap.pbi.dbms.domain;

import com.riap.listing.domain.model.PropertyType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ListingFilterTest {

    @Test
    void emptyFilterHasNoConstraints() {
        ListingFilter filter = ListingFilter.empty();

        assertNull(filter.keyword());
        assertNull(filter.city());
        assertNull(filter.propertyType());
        assertNull(filter.minRentCents());
        assertNull(filter.maxRentCents());
        assertNull(filter.hasInternet());
        assertNull(filter.hasFurniture());
        assertNull(filter.hasAC());
        assertNull(filter.petFriendly());
        assertNull(filter.hasParking());
        assertEquals(0, filter.page());
        assertEquals(10, filter.size());
        assertEquals(SortOrder.POSTED_AT_DESC, filter.sortOrder());
    }

    @Test
    void builderSetsAllFields() {
        ListingFilter filter = ListingFilter.builder()
                .keyword("台北 套房")
                .city("台北市")
                .propertyType(PropertyType.STUDIO)
                .minRentCents(10000L)
                .maxRentCents(30000L)
                .hasInternet(true)
                .hasFurniture(false)
                .hasAC(true)
                .petFriendly(false)
                .hasParking(true)
                .page(2)
                .size(20)
                .sortOrder(SortOrder.RENT_ASC)
                .build();

        assertEquals("台北 套房", filter.keyword());
        assertEquals("台北市", filter.city());
        assertEquals(PropertyType.STUDIO, filter.propertyType());
        assertEquals(10000L, filter.minRentCents());
        assertEquals(30000L, filter.maxRentCents());
        assertTrue(filter.hasInternet());
        assertFalse(filter.hasFurniture());
        assertTrue(filter.hasAC());
        assertFalse(filter.petFriendly());
        assertTrue(filter.hasParking());
        assertEquals(2, filter.page());
        assertEquals(20, filter.size());
        assertEquals(SortOrder.RENT_ASC, filter.sortOrder());
    }

    @Test
    void negativePageDefaultsToZero() {
        ListingFilter filter = ListingFilter.builder()
                .page(-1)
                .build();

        assertEquals(0, filter.page());
    }

    @Test
    void sizeClampedBetweenOneAndOneHundred() {
        ListingFilter tooSmall = ListingFilter.builder().size(0).build();
        ListingFilter tooLarge = ListingFilter.builder().size(200).build();

        assertEquals(1, tooSmall.size());
        assertEquals(100, tooLarge.size());
    }
}
