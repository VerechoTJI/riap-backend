package com.riap.pbi.controller;

import com.riap.listing.domain.model.ListingEntity;
import com.riap.listing.domain.model.ListingStatus;
import com.riap.listing.domain.model.PropertyType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record ListingResponse(
        String id,
        String title,
        String description,
        long rentCents,
        long depositCents,
        long managementFeeCents,
        String city,
        String district,
        String address,
        PropertyType propertyType,
        double sizePing,
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
    public static ListingResponse from(ListingEntity l) {
        long rentCents = l.getFeeDisclosure() != null && l.getFeeDisclosure().getRent() != null 
            ? l.getFeeDisclosure().getRent().multiply(new BigDecimal(100)).longValue() : 0;
        long depositCents = l.getFeeDisclosure() != null && l.getFeeDisclosure().getDeposit() != null 
            ? l.getFeeDisclosure().getDeposit().multiply(new BigDecimal(100)).longValue() : 0;
        long managementFeeCents = l.getFeeDisclosure() != null && l.getFeeDisclosure().getManagementFee() != null 
            ? l.getFeeDisclosure().getManagementFee().multiply(new BigDecimal(100)).longValue() : 0;

        return new ListingResponse(
                l.getId().toString(),
                l.getTitle(),
                l.getDescription(),
                rentCents,
                depositCents,
                managementFeeCents,
                l.getCity(),
                l.getDistrict(),
                l.getAddress(),
                l.getPropertyType(),
                l.getArea() != null ? l.getArea() : 0.0,
                l.getFloor() != null ? l.getFloor() : 0,
                l.getTotalFloors() != null ? l.getTotalFloors() : 0,
                Boolean.TRUE.equals(l.getHasInternet()),
                Boolean.TRUE.equals(l.getHasFurniture()),
                Boolean.TRUE.equals(l.getHasAC()),
                Boolean.TRUE.equals(l.getPetFriendly()),
                Boolean.TRUE.equals(l.getHasParking()),
                l.getAvailableFrom(),
                l.getPostedAt(),
                l.getImageUrl(),
                l.getStatus()
        );
    }
}
