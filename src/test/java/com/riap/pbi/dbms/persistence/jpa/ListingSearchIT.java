package com.riap.pbi.dbms.persistence.jpa;

import com.riap.pbi.dbms.domain.*;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ListingSearchIT {

    private static EntityManagerFactory emf;
    private static UserAccountJpaAdapter userAdapter;
    private static ListingJpaAdapter listingAdapter;
    private static Long landlordId;

    @BeforeAll
    static void setup() {
        emf = Persistence.createEntityManagerFactory("test-pu");
        userAdapter = new UserAccountJpaAdapter(emf);
        listingAdapter = new ListingJpaAdapter(emf);

        UserAccount landlord = UserAccount.create("search-landlord@example.com", "pwd",
                UserAccountRole.LANDLORD, UserAccountStatus.ACTIVE);
        landlordId = userAdapter.save(landlord).getId();

        listingAdapter.save(Listing.builder()
                .title("台北大安套房").rentCents(18000L).landlordId(landlordId)
                .city("台北市").district("大安區").propertyType(PropertyType.STUDIO)
                .sizePing(8).status(ListingStatus.AVAILABLE).hasInternet(true).build());

        listingAdapter.save(Listing.builder()
                .title("新北板橋雅房").rentCents(8000L).landlordId(landlordId)
                .city("新北市").district("板橋區").propertyType(PropertyType.BEDROOM)
                .sizePing(6).status(ListingStatus.AVAILABLE).hasInternet(false).build());

        listingAdapter.save(Listing.builder()
                .title("台北信義公寓").rentCents(35000L).landlordId(landlordId)
                .city("台北市").district("信義區").propertyType(PropertyType.APARTMENT)
                .sizePing(25).status(ListingStatus.AVAILABLE).hasInternet(true).petFriendly(true).build());

        listingAdapter.save(Listing.builder()
                .title("待審核物件").rentCents(12000L).landlordId(landlordId)
                .city("台北市").propertyType(PropertyType.STUDIO)
                .sizePing(7).status(ListingStatus.PENDING).build());
    }

    @AfterAll
    static void tearDown() {
        if (emf != null) emf.close();
    }

    @Test
    void searchWithNoCriteriaReturnsOnlyAvailableListings() {
        ListingFilter filter = ListingFilter.empty();
        List<Listing> results = listingAdapter.search(filter);

        assertEquals(3, results.size());
        results.forEach(l -> assertEquals(ListingStatus.AVAILABLE, l.getStatus()));
    }

    @Test
    void searchByCityFiltersResults() {
        ListingFilter filter = ListingFilter.builder().city("台北市").build();
        List<Listing> results = listingAdapter.search(filter);

        assertEquals(2, results.size());
        results.forEach(l -> assertEquals("台北市", l.getCity()));
    }

    @Test
    void searchByRentRangeFiltersResults() {
        ListingFilter filter = ListingFilter.builder()
                .minRentCents(10000L).maxRentCents(25000L).build();
        List<Listing> results = listingAdapter.search(filter);

        assertEquals(1, results.size());
        assertEquals("台北大安套房", results.get(0).getTitle());
    }

    @Test
    void searchByPropertyTypeFiltersResults() {
        ListingFilter filter = ListingFilter.builder()
                .propertyType(PropertyType.STUDIO).build();
        List<Listing> results = listingAdapter.search(filter);

        assertEquals(1, results.size());
        assertEquals(PropertyType.STUDIO, results.get(0).getPropertyType());
    }

    @Test
    void searchByAmenityFiltersResults() {
        ListingFilter filter = ListingFilter.builder().hasInternet(true).build();
        List<Listing> results = listingAdapter.search(filter);

        assertEquals(2, results.size());
        results.forEach(l -> assertTrue(l.isHasInternet()));
    }

    @Test
    void searchByKeywordMatchesTitleOrDistrict() {
        ListingFilter filter = ListingFilter.builder().keyword("大安").build();
        List<Listing> results = listingAdapter.search(filter);

        assertEquals(1, results.size());
        assertTrue(results.get(0).getTitle().contains("大安"));
    }

    @Test
    void paginationLimitsResults() {
        ListingFilter filter = ListingFilter.builder().page(0).size(2).build();
        List<Listing> results = listingAdapter.search(filter);

        assertEquals(2, results.size());
    }

    @Test
    void sortByRentAscReturnsOrderedResults() {
        ListingFilter filter = ListingFilter.builder().sortOrder(SortOrder.RENT_ASC).build();
        List<Listing> results = listingAdapter.search(filter);

        assertEquals(3, results.size());
        assertTrue(results.get(0).getRentCents() <= results.get(1).getRentCents());
        assertTrue(results.get(1).getRentCents() <= results.get(2).getRentCents());
    }
}
