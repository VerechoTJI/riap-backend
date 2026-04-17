package com.riap.pbi.dbms.domain;

import java.util.Objects;

public final class Listing {

    private final Long id;
    private final String title;
    private final long rentCents;
    private final Long landlordId;
    private final ListingStatus status;

    private Listing(Long id, String title, long rentCents, Long landlordId, ListingStatus status) {
        this.id = id;
        this.title = title;
        this.rentCents = rentCents;
        this.landlordId = landlordId;
        this.status = status;
    }

    public static Listing create(String title, long rentCents, Long landlordId) {
        Objects.requireNonNull(title);
        Objects.requireNonNull(landlordId);
        return new Listing(null, title, rentCents, landlordId, ListingStatus.AVAILABLE);
    }

    public static Listing rehydrate(Long id, String title, long rentCents, Long landlordId, ListingStatus status) {
        return new Listing(id, title, rentCents, landlordId, status);
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public long getRentCents() {
        return rentCents;
    }

    public Long getLandlordId() {
        return landlordId;
    }

    public ListingStatus getStatus() {
        return status;
    }
}
