package com.riap.pbi.rcs.infrastructure.fake;

import com.riap.pbi.rcs.port.LmsClient;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import java.util.UUID;

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
    public UUID getLandlordIdForListing(String listingId) {
        // In the prototype, Bob Wang is landlord with id "2".
        return UUID.fromString("00000000-0000-0000-0000-000000000002");
    }

    @Override
    public Map<String, Object> getListingSummary(String listingId) {
        Map<String, Object> map = new HashMap<>();
        map.put("title", titles.getOrDefault(listingId, "Fake Listing Summary for " + listingId));
        map.put("landlordId", UUID.fromString("00000000-0000-0000-0000-000000000002"));
        return map;
    }
}
