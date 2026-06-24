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

@RestController
@RequestMapping("/api/listings")
public class ListingSearchController {

    private final ListingSearchService listingSearchService;

    public ListingSearchController(ListingSearchService listingSearchService) {
        this.listingSearchService = listingSearchService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String propertyType,
            @RequestParam(required = false) Long minRent,
            @RequestParam(required = false) Long maxRent,
            @RequestParam(required = false) Boolean hasInternet,
            @RequestParam(required = false) Boolean hasFurniture,
            @RequestParam(required = false) Boolean hasAC,
            @RequestParam(required = false) Boolean petFriendly,
            @RequestParam(required = false) Boolean hasParking,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort
    ) {
        ListingFilter filter = ListingFilter.builder()
                .keyword(keyword)
                .city(city)
                .propertyType(propertyType != null ? PropertyType.valueOf(propertyType) : null)
                .minRentCents(minRent)
                .maxRentCents(maxRent)
                .hasInternet(hasInternet)
                .hasFurniture(hasFurniture)
                .hasAC(hasAC)
                .petFriendly(petFriendly)
                .hasParking(hasParking)
                .page(page)
                .size(size)
                .sortOrder(sort != null ? SortOrder.valueOf(sort) : null)
                .build();

        List<ListingResponse> listings = listingSearchService.search(filter)
                .stream()
                .map(ListingResponse::from)
                .toList();

        return ResponseEntity.ok(Map.of("listings", listings));
    }
}
