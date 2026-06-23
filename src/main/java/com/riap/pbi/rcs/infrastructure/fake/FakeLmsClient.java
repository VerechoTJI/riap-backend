package com.riap.pbi.rcs.infrastructure.fake;

import com.riap.pbi.rcs.port.LmsClient;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class FakeLmsClient implements LmsClient {
    private final Map<String, String> titles = new HashMap<>();

    public FakeLmsClient() {
        titles.put("1", "台北市中正區採光套房");
        titles.put("2", "台北市大安區美式別墅");
        titles.put("3", "新北市河景雅房");
        titles.put("4", "台中西區機能兩房");
        
        // fallback
        titles.put("list-1", "Beautiful Apartment");
    }

    @Override
    public String getLandlordIdForListing(String listingId) {
        // In the prototype, Bob Wang is landlord with id "2".
        return "2";
    }

    @Override
    public String getListingSummary(String listingId) {
        return titles.getOrDefault(listingId, "Fake Listing Summary for " + listingId);
    }
}
