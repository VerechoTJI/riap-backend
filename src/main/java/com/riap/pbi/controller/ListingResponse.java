package com.riap.pbi.controller;

import com.riap.pbi.dbms.domain.Listing;
import com.riap.pbi.dbms.domain.ListingStatus;
import com.riap.pbi.dbms.domain.PropertyType;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ListingResponse(
        Long id,
        String title,
        String description,
        long rentCents,
        long depositCents,
        long managementFeeCents,
        String city,
        String district,
        String address,
        PropertyType propertyType,
        int sizePing,
        int floor,
        int totalFloors,
        boolean hasInternet,
        boolean hasFurniture,
        boolean hasAC,
        boolean petFriendly,
        boolean hasParking,
        LocalDate availableFrom,
        LocalDateTime postedAt,
        String imageUrl,
        ListingStatus status
) {
    static ListingResponse from(Listing l) {
        return new ListingResponse(
                l.getId(),
                l.getTitle(),
                l.getDescription(),
                l.getRentCents(),
                l.getDepositCents(),
                l.getManagementFeeCents(),
                l.getCity(),
                l.getDistrict(),
                l.getAddress(),
                l.getPropertyType(),
                l.getSizePing(),
                l.getFloor(),
                l.getTotalFloors(),
                l.isHasInternet(),
                l.isHasFurniture(),
                l.isHasAC(),
                l.isPetFriendly(),
                l.isHasParking(),
                l.getAvailableFrom(),
                l.getPostedAt(),
                l.getImageUrl(),
                l.getStatus()
        );
    }
}
