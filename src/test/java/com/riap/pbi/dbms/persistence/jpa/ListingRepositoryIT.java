package com.riap.pbi.dbms.persistence.jpa;

import com.riap.pbi.dbms.domain.Listing;
import com.riap.pbi.dbms.domain.UserAccount;
import com.riap.pbi.dbms.domain.UserAccountRole;
import com.riap.pbi.dbms.domain.UserAccountStatus;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Optional;

class ListingRepositoryIT {

    private static EntityManagerFactory emf;

    @BeforeAll
    static void setup() {
        emf = Persistence.createEntityManagerFactory("test-pu");
    }

    @AfterAll
    static void tearDown() {
        if (emf != null) emf.close();
    }

    @Test
    void saveListingWithExistingLandlord() {
        UserAccountJpaAdapter userAdapter = new UserAccountJpaAdapter(emf);
        ListingJpaAdapter listingAdapter = new ListingJpaAdapter(emf);

        UserAccount landlord = UserAccount.create("landlord-it@example.com", "pwd", UserAccountRole.LANDLORD, UserAccountStatus.ACTIVE);
        UserAccount savedLandlord = userAdapter.save(landlord);

        Listing listing = Listing.create("Cozy Studio", 25000L, savedLandlord.getId());
        Listing saved = listingAdapter.save(listing);

        Assertions.assertNotNull(saved.getId());

        Optional<Listing> found = listingAdapter.findById(saved.getId());
        Assertions.assertTrue(found.isPresent());
        Assertions.assertEquals(saved.getTitle(), found.get().getTitle());
        Assertions.assertEquals(savedLandlord.getId(), found.get().getLandlordId());

        listingAdapter.deleteById(saved.getId());
        Assertions.assertTrue(listingAdapter.findById(saved.getId()).isEmpty());
    }
}
