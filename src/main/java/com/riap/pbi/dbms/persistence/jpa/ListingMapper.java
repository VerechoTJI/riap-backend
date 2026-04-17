package com.riap.pbi.dbms.persistence.jpa;

import com.riap.pbi.dbms.domain.Listing;

public final class ListingMapper {

    private ListingMapper() {}

    public static Listing toDomain(ListingEntity e) {
        if (e == null) return null;
        return Listing.rehydrate(e.getId(), e.getTitle(), e.getRentCents(), e.getLandlord().getId(), e.getStatus());
    }

    public static ListingEntity toEntity(Listing d, UserAccountEntity landlordEntity) {
        if (d == null) return null;
        return new ListingEntity(d.getId(), d.getTitle(), d.getRentCents(), landlordEntity, d.getStatus());
    }
}
