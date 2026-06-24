package com.riap.pbi.rcs.infrastructure.lms;

import com.riap.listing.domain.model.ListingEntity;
import com.riap.listing.domain.repository.ListingRepository;
import com.riap.pbi.rcs.port.LmsClient;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class LmsIntegrationAdapter implements LmsClient {

    private final ListingRepository listingRepository;

    public LmsIntegrationAdapter(ListingRepository listingRepository) {
        this.listingRepository = listingRepository;
    }

    @Override
    public UUID getLandlordIdForListing(String listingId) {
        return listingRepository.findById(UUID.fromString(listingId))
                .map(ListingEntity::getLandlordId)
                .orElse(null);
    }

    @Override
    public Map<String, Object> getListingSummary(String listingId) {
        Map<String, Object> map = new HashMap<>();
        ListingEntity listing = listingRepository.findById(UUID.fromString(listingId)).orElse(null);
        if (listing != null) {
            map.put("title", listing.getTitle());
            map.put("imageUrl", listing.getImageUrl());
            map.put("city", listing.getCity());
            map.put("landlordId", listing.getLandlordId());
        } else {
            map.put("title", "未知的房源");
            map.put("imageUrl", "");
            map.put("city", "");
            map.put("landlordId", null);
        }
        return map;
    }
}
