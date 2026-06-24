package com.riap.pbi.controller;

import com.riap.pbi.dbms.domain.ListingFilter;
import com.riap.listing.domain.model.PropertyType;
import com.riap.pbi.dbms.domain.SortOrder;
import com.riap.pbi.service.ListingSearchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.web.bind.annotation.PathVariable;

import com.riap.user.domain.repository.UserAccountRepository;

@RestController
@RequestMapping("/api/listings")
public class ListingSearchController {

    private final ListingSearchService listingSearchService;
    private final UserAccountRepository userAccountRepository;

    public ListingSearchController(ListingSearchService listingSearchService, UserAccountRepository userAccountRepository) {
        this.listingSearchService = listingSearchService;
        this.userAccountRepository = userAccountRepository;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> search(
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "city", required = false) String city,
            @RequestParam(name = "propertyType", required = false) String propertyType,
            @RequestParam(name = "minRent", required = false) Long minRent,
            @RequestParam(name = "maxRent", required = false) Long maxRent,
            @RequestParam(name = "hasInternet", required = false) Boolean hasInternet,
            @RequestParam(name = "hasFurniture", required = false) Boolean hasFurniture,
            @RequestParam(name = "hasAC", required = false) Boolean hasAC,
            @RequestParam(name = "petFriendly", required = false) Boolean petFriendly,
            @RequestParam(name = "hasParking", required = false) Boolean hasParking,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "sort", required = false) String sort
    ) {
        ListingFilter filter;
        try {
            filter = ListingFilter.builder()
                    .keyword(keyword)
                    .city(city)
                    .propertyType(propertyType != null && !propertyType.isEmpty() ? PropertyType.valueOf(propertyType) : null)
                    .minRentCents(minRent)
                    .maxRentCents(maxRent)
                    .hasInternet(hasInternet)
                    .hasFurniture(hasFurniture)
                    .hasAC(hasAC)
                    .petFriendly(petFriendly)
                    .hasParking(hasParking)
                    .page(page)
                    .size(size)
                    .sortOrder(sort != null && !sort.isEmpty() ? SortOrder.valueOf(sort) : null)
                    .build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid propertyType or sort order"));
        }

        List<ListingResponse> listings = listingSearchService.search(filter)
                .stream()
                .map(entity -> {
                    String landlordName = "房東";
                    if (entity.getLandlordId() != null) {
                        landlordName = userAccountRepository.findById(entity.getLandlordId())
                                .map(com.riap.user.domain.model.UserAccountEntity::getLoginIdentifier)
                                .map(name -> name.substring(0, 1).toUpperCase() + name.substring(1))
                                .orElse("房東");
                    }
                    return ListingResponse.from(entity, landlordName);
                })
                .toList();

        return ResponseEntity.ok(Map.of("listings", listings));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ListingResponse> getById(@PathVariable(name = "id") UUID id) {
        try {
            com.riap.listing.domain.model.ListingEntity entity = listingSearchService.findById(id);
            String landlordName = "房東";
            if (entity.getLandlordId() != null) {
                landlordName = userAccountRepository.findById(entity.getLandlordId())
                        .map(com.riap.user.domain.model.UserAccountEntity::getLoginIdentifier)
                        .map(name -> name.substring(0, 1).toUpperCase() + name.substring(1))
                        .orElse("房東");
            }
            return ResponseEntity.ok(ListingResponse.from(entity, landlordName));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
