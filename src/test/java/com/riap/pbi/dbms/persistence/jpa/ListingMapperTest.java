package com.riap.pbi.dbms.persistence.jpa;

import com.riap.pbi.dbms.domain.Listing;
import com.riap.pbi.dbms.domain.ListingStatus;
import com.riap.pbi.dbms.domain.PropertyType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ListingMapperTest {

    @Test
    void mapsBetweenDomainAndEntity() {
        UserAccountEntity landlord = new UserAccountEntity(5L, "landlord@example.com", "hash",
                com.riap.pbi.dbms.domain.UserAccountRole.LANDLORD,
                com.riap.pbi.dbms.domain.UserAccountStatus.ACTIVE);

        Listing domain = Listing.builder()
                .id(11L)
                .title("Nice Apt")
                .rentCents(35000L)
                .landlordId(5L)
                .city("台北市")
                .propertyType(PropertyType.APARTMENT)
                .sizePing(15)
                .status(ListingStatus.AVAILABLE)
                .build();

        ListingEntity entity = ListingMapper.toEntity(domain, landlord);
        Listing mapped = ListingMapper.toDomain(entity);

        Assertions.assertEquals(domain.getId(), mapped.getId());
        Assertions.assertEquals(domain.getTitle(), mapped.getTitle());
        Assertions.assertEquals(domain.getRentCents(), mapped.getRentCents());
        Assertions.assertEquals(domain.getLandlordId(), mapped.getLandlordId());
        Assertions.assertEquals(domain.getStatus(), mapped.getStatus());
        Assertions.assertEquals(domain.getCity(), mapped.getCity());
        Assertions.assertEquals(domain.getPropertyType(), mapped.getPropertyType());
    }
}
