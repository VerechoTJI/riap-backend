package com.riap.pbi.dbms.persistence.jpa;

import jakarta.persistence.*;

@Entity
@Table(name = "listings")
public class ListingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(name = "rent_cents", nullable = false)
    private long rentCents;

    @ManyToOne(optional = false)
    @JoinColumn(name = "landlord_id", referencedColumnName = "id", nullable = false)
    private UserAccountEntity landlord;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private com.riap.pbi.dbms.domain.ListingStatus status;

    protected ListingEntity() {
    }

    public ListingEntity(Long id, String title, long rentCents, UserAccountEntity landlord, com.riap.pbi.dbms.domain.ListingStatus status) {
        this.id = id;
        this.title = title;
        this.rentCents = rentCents;
        this.landlord = landlord;
        this.status = status;
    }

    public Long getId() { return id; }

    public String getTitle() { return title; }

    public long getRentCents() { return rentCents; }

    public UserAccountEntity getLandlord() { return landlord; }

    public com.riap.pbi.dbms.domain.ListingStatus getStatus() { return status; }
}
