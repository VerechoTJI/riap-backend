package com.riap.pbi.dbms.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public final class Listing {

    private final Long id;
    private final String title;
    private final String description;
    private final long rentCents;
    private final long depositCents;
    private final long managementFeeCents;
    private final Long landlordId;
    private final ListingStatus status;
    private final String city;
    private final String district;
    private final String address;
    private final PropertyType propertyType;
    private final int sizePing;
    private final int floor;
    private final int totalFloors;
    private final boolean hasInternet;
    private final boolean hasFurniture;
    private final boolean hasAC;
    private final boolean petFriendly;
    private final boolean hasParking;
    private final LocalDate availableFrom;
    private final LocalDateTime postedAt;
    private final String imageUrl;

    private Listing(Builder b) {
        this.id = b.id;
        this.title = b.title;
        this.description = b.description;
        this.rentCents = b.rentCents;
        this.depositCents = b.depositCents;
        this.managementFeeCents = b.managementFeeCents;
        this.landlordId = b.landlordId;
        this.status = b.status;
        this.city = b.city;
        this.district = b.district;
        this.address = b.address;
        this.propertyType = b.propertyType;
        this.sizePing = b.sizePing;
        this.floor = b.floor;
        this.totalFloors = b.totalFloors;
        this.hasInternet = b.hasInternet;
        this.hasFurniture = b.hasFurniture;
        this.hasAC = b.hasAC;
        this.petFriendly = b.petFriendly;
        this.hasParking = b.hasParking;
        this.availableFrom = b.availableFrom;
        this.postedAt = b.postedAt;
        this.imageUrl = b.imageUrl;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Long id;
        private String title;
        private String description;
        private long rentCents;
        private long depositCents;
        private long managementFeeCents;
        private Long landlordId;
        private ListingStatus status;
        private String city;
        private String district;
        private String address;
        private PropertyType propertyType;
        private int sizePing;
        private int floor;
        private int totalFloors;
        private boolean hasInternet;
        private boolean hasFurniture;
        private boolean hasAC;
        private boolean petFriendly;
        private boolean hasParking;
        private LocalDate availableFrom;
        private LocalDateTime postedAt;
        private String imageUrl;

        private Builder() {}

        public Builder id(Long id)                           { this.id = id; return this; }
        public Builder title(String title)                   { this.title = title; return this; }
        public Builder description(String description)       { this.description = description; return this; }
        public Builder rentCents(long rentCents)             { this.rentCents = rentCents; return this; }
        public Builder depositCents(long depositCents)       { this.depositCents = depositCents; return this; }
        public Builder managementFeeCents(long v)            { this.managementFeeCents = v; return this; }
        public Builder landlordId(Long landlordId)           { this.landlordId = landlordId; return this; }
        public Builder status(ListingStatus status)          { this.status = status; return this; }
        public Builder city(String city)                     { this.city = city; return this; }
        public Builder district(String district)             { this.district = district; return this; }
        public Builder address(String address)               { this.address = address; return this; }
        public Builder propertyType(PropertyType propertyType) { this.propertyType = propertyType; return this; }
        public Builder sizePing(int sizePing)                { this.sizePing = sizePing; return this; }
        public Builder floor(int floor)                      { this.floor = floor; return this; }
        public Builder totalFloors(int totalFloors)          { this.totalFloors = totalFloors; return this; }
        public Builder hasInternet(boolean hasInternet)      { this.hasInternet = hasInternet; return this; }
        public Builder hasFurniture(boolean hasFurniture)    { this.hasFurniture = hasFurniture; return this; }
        public Builder hasAC(boolean hasAC)                  { this.hasAC = hasAC; return this; }
        public Builder petFriendly(boolean petFriendly)      { this.petFriendly = petFriendly; return this; }
        public Builder hasParking(boolean hasParking)        { this.hasParking = hasParking; return this; }
        public Builder availableFrom(LocalDate availableFrom) { this.availableFrom = availableFrom; return this; }
        public Builder postedAt(LocalDateTime postedAt)      { this.postedAt = postedAt; return this; }
        public Builder imageUrl(String imageUrl)             { this.imageUrl = imageUrl; return this; }

        public Listing build() {
            Objects.requireNonNull(title, "title is required");
            Objects.requireNonNull(landlordId, "landlordId is required");
            Objects.requireNonNull(city, "city is required");
            Objects.requireNonNull(propertyType, "propertyType is required");
            if (status == null) {
                status = ListingStatus.PENDING;
            }
            return new Listing(this);
        }
    }

    public Long getId()                     { return id; }
    public String getTitle()                { return title; }
    public String getDescription()          { return description; }
    public long getRentCents()              { return rentCents; }
    public long getDepositCents()           { return depositCents; }
    public long getManagementFeeCents()     { return managementFeeCents; }
    public Long getLandlordId()             { return landlordId; }
    public ListingStatus getStatus()        { return status; }
    public String getCity()                 { return city; }
    public String getDistrict()             { return district; }
    public String getAddress()              { return address; }
    public PropertyType getPropertyType()   { return propertyType; }
    public int getSizePing()                { return sizePing; }
    public int getFloor()                   { return floor; }
    public int getTotalFloors()             { return totalFloors; }
    public boolean isHasInternet()          { return hasInternet; }
    public boolean isHasFurniture()         { return hasFurniture; }
    public boolean isHasAC()               { return hasAC; }
    public boolean isPetFriendly()          { return petFriendly; }
    public boolean isHasParking()           { return hasParking; }
    public LocalDate getAvailableFrom()     { return availableFrom; }
    public LocalDateTime getPostedAt()      { return postedAt; }
    public String getImageUrl()             { return imageUrl; }
}
