package com.riap.pbi.rcs.infrastructure.fake;

import com.riap.pbi.rcs.port.LmsClient;
import org.springframework.stereotype.Component;

@Component
public class FakeLmsClient implements LmsClient {
    @Override
    public String getLandlordIdForListing(String listingId) {
        // In the prototype, Bob Wang is landlord with id "2".
        return "2";
    }

    @Override
    public String getListingSummary(String listingId) {
        return "Fake Listing Summary for " + listingId;
    }
}
