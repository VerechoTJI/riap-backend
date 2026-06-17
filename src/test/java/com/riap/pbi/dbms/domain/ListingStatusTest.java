package com.riap.pbi.dbms.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ListingStatusTest {

    @Test
    void hasAllFiveStatuses() {
        assertNotNull(ListingStatus.PENDING);
        assertNotNull(ListingStatus.AVAILABLE);
        assertNotNull(ListingStatus.REJECTED);
        assertNotNull(ListingStatus.RENTED);
        assertNotNull(ListingStatus.REMOVED);
    }

    @Test
    void pendingNameMatchesString() {
        assertEquals("PENDING", ListingStatus.PENDING.name());
    }

    @Test
    void rejectedNameMatchesString() {
        assertEquals("REJECTED", ListingStatus.REJECTED.name());
    }
}
