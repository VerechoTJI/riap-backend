package com.riap.pbi.rcs.port;

public interface LmsClient {
    /**
     * Gets the landlord ID for a specific listing.
     * @param listingId The listing ID.
     * @return The landlord ID.
     */
    String getLandlordIdForListing(String listingId);
    
    /**
     * Gets a short summary of a listing.
     * @param listingId The listing ID.
     * @return Summary string of the listing.
     */
    String getListingSummary(String listingId);
}
