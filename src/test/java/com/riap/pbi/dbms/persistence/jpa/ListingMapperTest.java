package com.riap.pbi.dbms.persistence.jpa;

import com.riap.pbi.dbms.domain.Listing;
import com.riap.pbi.dbms.domain.ListingStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ListingMapperTest {

    @Test
    void mapsBetweenDomainAndEntity() {
        UserAccountEntity landlord = new UserAccountEntity(5L, "landlord@example.com", "hash", com.riap.pbi.dbms.domain.UserAccountRole.LANDLORD, com.riap.pbi.dbms.domain.UserAccountStatus.ACTIVE);

        com.riap.pbi.dbms.domain.Listing domain = com.riap.pbi.dbms.domain.Listing.rehydrate(11L, "Nice Apt", 35000L, 5L, ListingStatus.AVAILABLE);

        ListingEntity entity = ListingMapper.toEntity(domain, landlord);
        com.riap.pbi.dbms.domain.Listing mapped = ListingMapper.toDomain(entity);

        Assertions.assertEquals(domain.getId(), mapped.getId());
        Assertions.assertEquals(domain.getTitle(), mapped.getTitle());
        Assertions.assertEquals(domain.getRentCents(), mapped.getRentCents());
        Assertions.assertEquals(domain.getLandlordId(), mapped.getLandlordId());
        Assertions.assertEquals(domain.getStatus(), mapped.getStatus());
    }
}
