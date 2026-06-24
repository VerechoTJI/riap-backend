package com.riap.listing.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "listings")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String title;
    private String description;
    private Double area;
    private Integer floor;

    @Enumerated(EnumType.STRING)
    private PropertyType propertyType;

    @Enumerated(EnumType.STRING)
    private ListingStatus status;

    @Embedded
    private FeeDisclosure feeDisclosure;

    private String city;
    private String district;
    private String address;
    private Integer totalFloors;

    private Boolean hasInternet;
    private Boolean hasFurniture;
    private Boolean hasAC;
    private Boolean petFriendly;
    private Boolean hasParking;

    private LocalDate availableFrom;
    private LocalDateTime postedAt;
    private String imageUrl;

    private UUID landlordId;
    private String returnReason;
}
