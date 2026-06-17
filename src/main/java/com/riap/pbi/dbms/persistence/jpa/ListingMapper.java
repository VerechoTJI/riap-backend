package com.riap.pbi.dbms.persistence.jpa;

import com.riap.pbi.dbms.domain.Listing;

public final class ListingMapper {

    private ListingMapper() {}

    public static Listing toDomain(ListingEntity e) {
        if (e == null) return null;
        return Listing.builder()
                .id(e.getId())
                .title(e.getTitle())
                .description(e.getDescription())
                .rentCents(e.getRentCents())
                .depositCents(e.getDepositCents())
                .managementFeeCents(e.getManagementFeeCents())
                .landlordId(e.getLandlord().getId())
                .status(e.getStatus())
                .city(e.getCity())
                .district(e.getDistrict())
                .address(e.getAddress())
                .propertyType(e.getPropertyType())
                .sizePing(e.getSizePing())
                .floor(e.getFloor())
                .totalFloors(e.getTotalFloors())
                .hasInternet(e.isHasInternet())
                .hasFurniture(e.isHasFurniture())
                .hasAC(e.isHasAC())
                .petFriendly(e.isPetFriendly())
                .hasParking(e.isHasParking())
                .availableFrom(e.getAvailableFrom())
                .postedAt(e.getPostedAt())
                .imageUrl(e.getImageUrl())
                .build();
    }

    public static ListingEntity toEntity(Listing d, UserAccountEntity landlordEntity) {
        if (d == null) return null;
        ListingEntity e = new ListingEntity();
        e.setId(d.getId());
        e.setTitle(d.getTitle());
        e.setDescription(d.getDescription());
        e.setRentCents(d.getRentCents());
        e.setDepositCents(d.getDepositCents());
        e.setManagementFeeCents(d.getManagementFeeCents());
        e.setLandlord(landlordEntity);
        e.setStatus(d.getStatus());
        e.setCity(d.getCity());
        e.setDistrict(d.getDistrict());
        e.setAddress(d.getAddress());
        e.setPropertyType(d.getPropertyType());
        e.setSizePing(d.getSizePing());
        e.setFloor(d.getFloor());
        e.setTotalFloors(d.getTotalFloors());
        e.setHasInternet(d.isHasInternet());
        e.setHasFurniture(d.isHasFurniture());
        e.setHasAC(d.isHasAC());
        e.setPetFriendly(d.isPetFriendly());
        e.setHasParking(d.isHasParking());
        e.setAvailableFrom(d.getAvailableFrom());
        e.setPostedAt(d.getPostedAt());
        e.setImageUrl(d.getImageUrl());
        return e;
    }
}
