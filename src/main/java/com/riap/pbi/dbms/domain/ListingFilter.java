package com.riap.pbi.dbms.domain;

import com.riap.listing.domain.model.PropertyType;

public final class ListingFilter {

    private final String keyword;
    private final String city;
    private final PropertyType propertyType;
    private final Long minRentCents;
    private final Long maxRentCents;
    private final Boolean hasInternet;
    private final Boolean hasFurniture;
    private final Boolean hasAC;
    private final Boolean petFriendly;
    private final Boolean hasParking;
    private final int page;
    private final int size;
    private final SortOrder sortOrder;

    private ListingFilter(Builder b) {
        this.keyword = b.keyword;
        this.city = b.city;
        this.propertyType = b.propertyType;
        this.minRentCents = b.minRentCents;
        this.maxRentCents = b.maxRentCents;
        this.hasInternet = b.hasInternet;
        this.hasFurniture = b.hasFurniture;
        this.hasAC = b.hasAC;
        this.petFriendly = b.petFriendly;
        this.hasParking = b.hasParking;
        this.page = Math.max(0, b.page);
        this.size = Math.min(100, Math.max(1, b.size));
        this.sortOrder = b.sortOrder != null ? b.sortOrder : SortOrder.POSTED_AT_DESC;
    }

    public static ListingFilter empty() {
        return new Builder().build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String keyword;
        private String city;
        private PropertyType propertyType;
        private Long minRentCents;
        private Long maxRentCents;
        private Boolean hasInternet;
        private Boolean hasFurniture;
        private Boolean hasAC;
        private Boolean petFriendly;
        private Boolean hasParking;
        private int page = 0;
        private int size = 10;
        private SortOrder sortOrder;

        private Builder() {}

        public Builder keyword(String keyword)           { this.keyword = keyword; return this; }
        public Builder city(String city)                 { this.city = city; return this; }
        public Builder propertyType(PropertyType t)      { this.propertyType = t; return this; }
        public Builder minRentCents(Long v)              { this.minRentCents = v; return this; }
        public Builder maxRentCents(Long v)              { this.maxRentCents = v; return this; }
        public Builder hasInternet(Boolean v)            { this.hasInternet = v; return this; }
        public Builder hasFurniture(Boolean v)           { this.hasFurniture = v; return this; }
        public Builder hasAC(Boolean v)                  { this.hasAC = v; return this; }
        public Builder petFriendly(Boolean v)            { this.petFriendly = v; return this; }
        public Builder hasParking(Boolean v)             { this.hasParking = v; return this; }
        public Builder page(int page)                    { this.page = page; return this; }
        public Builder size(int size)                    { this.size = size; return this; }
        public Builder sortOrder(SortOrder sortOrder)    { this.sortOrder = sortOrder; return this; }

        public ListingFilter build() {
            return new ListingFilter(this);
        }
    }

    public String keyword()           { return keyword; }
    public String city()              { return city; }
    public PropertyType propertyType() { return propertyType; }
    public Long minRentCents()        { return minRentCents; }
    public Long maxRentCents()        { return maxRentCents; }
    public Boolean hasInternet()      { return hasInternet; }
    public Boolean hasFurniture()     { return hasFurniture; }
    public Boolean hasAC()            { return hasAC; }
    public Boolean petFriendly()      { return petFriendly; }
    public Boolean hasParking()       { return hasParking; }
    public int page()                 { return page; }
    public int size()                 { return size; }
    public SortOrder sortOrder()      { return sortOrder; }
}
