package com.riap.pbi.rcs.port;

import java.util.Map;
import java.util.UUID;

public interface LmsClient {
    /**
     * Gets the landlord ID for a specific listing.
     * @param listingId The listing ID.
     * @return The landlord ID.
     */
    UUID getLandlordIdForListing(String listingId);
    
    /**
     * Gets a short summary of a listing.
     * @param listingId The listing ID.
     * @return Summary map of the listing, including 'title' and 'landlordId'.
     */
    Map<String, Object> getListingSummary(String listingId);
}
