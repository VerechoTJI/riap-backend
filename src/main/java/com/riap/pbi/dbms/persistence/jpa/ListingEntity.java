package com.riap.pbi.dbms.persistence.jpa;

import com.riap.pbi.dbms.domain.ListingStatus;
import com.riap.pbi.dbms.domain.PropertyType;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "listings")
public class ListingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "rent_cents", nullable = false)
    private long rentCents;

    @Column(name = "deposit_cents")
    private long depositCents;

    @Column(name = "management_fee_cents")
    private long managementFeeCents;

    @ManyToOne(optional = false)
    @JoinColumn(name = "landlord_id", referencedColumnName = "id", nullable = false)
    private UserAccountEntity landlord;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private ListingStatus status;

    @Column(name = "city", nullable = false, length = 50)
    private String city;

    @Column(name = "district", length = 50)
    private String district;

    @Column(name = "address", length = 255)
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(name = "property_type", length = 20, nullable = false)
    private PropertyType propertyType;

    @Column(name = "size_ping")
    private int sizePing;

    @Column(name = "floor")
    private int floor;

    @Column(name = "total_floors")
    private int totalFloors;

    @Column(name = "has_internet")
    private boolean hasInternet;

    @Column(name = "has_furniture")
    private boolean hasFurniture;

    @Column(name = "has_ac")
    private boolean hasAC;

    @Column(name = "pet_friendly")
    private boolean petFriendly;

    @Column(name = "has_parking")
    private boolean hasParking;

    @Column(name = "available_from")
    private LocalDate availableFrom;

    @Column(name = "posted_at")
    private LocalDateTime postedAt;

    @Column(name = "image_url", length = 512)
    private String imageUrl;

    protected ListingEntity() {}

    public Long getId()                     { return id; }
    public void setId(Long id)              { this.id = id; }
    public String getTitle()                { return title; }
    public void setTitle(String title)      { this.title = title; }
    public String getDescription()          { return description; }
    public void setDescription(String d)    { this.description = d; }
    public long getRentCents()              { return rentCents; }
    public void setRentCents(long v)        { this.rentCents = v; }
    public long getDepositCents()           { return depositCents; }
    public void setDepositCents(long v)     { this.depositCents = v; }
    public long getManagementFeeCents()     { return managementFeeCents; }
    public void setManagementFeeCents(long v) { this.managementFeeCents = v; }
    public UserAccountEntity getLandlord()  { return landlord; }
    public void setLandlord(UserAccountEntity l) { this.landlord = l; }
    public ListingStatus getStatus()        { return status; }
    public void setStatus(ListingStatus s)  { this.status = s; }
    public String getCity()                 { return city; }
    public void setCity(String city)        { this.city = city; }
    public String getDistrict()             { return district; }
    public void setDistrict(String d)       { this.district = d; }
    public String getAddress()              { return address; }
    public void setAddress(String a)        { this.address = a; }
    public PropertyType getPropertyType()   { return propertyType; }
    public void setPropertyType(PropertyType p) { this.propertyType = p; }
    public int getSizePing()                { return sizePing; }
    public void setSizePing(int v)          { this.sizePing = v; }
    public int getFloor()                   { return floor; }
    public void setFloor(int v)             { this.floor = v; }
    public int getTotalFloors()             { return totalFloors; }
    public void setTotalFloors(int v)       { this.totalFloors = v; }
    public boolean isHasInternet()          { return hasInternet; }
    public void setHasInternet(boolean v)   { this.hasInternet = v; }
    public boolean isHasFurniture()         { return hasFurniture; }
    public void setHasFurniture(boolean v)  { this.hasFurniture = v; }
    public boolean isHasAC()               { return hasAC; }
    public void setHasAC(boolean v)         { this.hasAC = v; }
    public boolean isPetFriendly()          { return petFriendly; }
    public void setPetFriendly(boolean v)   { this.petFriendly = v; }
    public boolean isHasParking()           { return hasParking; }
    public void setHasParking(boolean v)    { this.hasParking = v; }
    public LocalDate getAvailableFrom()     { return availableFrom; }
    public void setAvailableFrom(LocalDate v) { this.availableFrom = v; }
    public LocalDateTime getPostedAt()      { return postedAt; }
    public void setPostedAt(LocalDateTime v) { this.postedAt = v; }
    public String getImageUrl()             { return imageUrl; }
    public void setImageUrl(String v)       { this.imageUrl = v; }
}
