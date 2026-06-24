package com.riap.pbi.integration;

import com.riap.listing.domain.model.FeeDisclosure;
import com.riap.listing.domain.model.ListingEntity;
import com.riap.listing.domain.model.ListingStatus;
import com.riap.listing.domain.model.PropertyType;
import com.riap.listing.domain.repository.ListingRepository;
import com.riap.pbi.controller.ListingResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class ListingSearchIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ListingRepository listingRepository;

    private UUID publishedListing1Id;
    private UUID publishedListing2Id;
    private UUID pendingListingId;
    private UUID privateListingId;

    @BeforeEach
    void setUp() {
        listingRepository.deleteAll();

        // 建立測試資料
        ListingEntity l1 = ListingEntity.builder()
                .title("台北信義高級公寓")
                .description("近捷運，生活機能佳")
                .city("台北市").district("信義區")
                .propertyType(PropertyType.APARTMENT)
                .status(ListingStatus.PUBLISHED)
                .feeDisclosure(FeeDisclosure.builder()
                        .rent(new BigDecimal("35000.00"))
                        .deposit(new BigDecimal("70000.00"))
                        .managementFee(new BigDecimal("2000.00"))
                        .build())
                .hasInternet(true).hasFurniture(true).hasAC(true).petFriendly(false)
                .landlordId("landlord-1")
                .build();
        publishedListing1Id = listingRepository.save(l1).getId();

        ListingEntity l2 = ListingEntity.builder()
                .title("新北板橋便宜雅房")
                .description("靜巷內，適合學生")
                .city("新北市").district("板橋區")
                .propertyType(PropertyType.SUITE)
                .status(ListingStatus.PUBLISHED)
                .feeDisclosure(FeeDisclosure.builder()
                        .rent(new BigDecimal("8000.00"))
                        .deposit(new BigDecimal("16000.00"))
                        .managementFee(BigDecimal.ZERO)
                        .build())
                .hasInternet(false).hasFurniture(true).hasAC(false).petFriendly(true)
                .landlordId("landlord-2")
                .build();
        publishedListing2Id = listingRepository.save(l2).getId();

        ListingEntity l3 = ListingEntity.builder()
                .title("待審核套房")
                .city("台北市")
                .status(ListingStatus.PENDING)
                .feeDisclosure(FeeDisclosure.builder().rent(new BigDecimal("10000")).build())
                .build();
        pendingListingId = listingRepository.save(l3).getId();
        
        ListingEntity l4 = ListingEntity.builder()
                .title("已下架公寓")
                .city("桃園市")
                .status(ListingStatus.PRIVATE)
                .feeDisclosure(FeeDisclosure.builder().rent(new BigDecimal("12000")).build())
                .build();
        privateListingId = listingRepository.save(l4).getId();
    }

    // [LSS-TC01] 驗證租屋列表查詢功能 (僅顯示 AVAILABLE / PUBLISHED)
    @Test
    void testSearchListings_OnlyReturnsPublished() {
        ResponseEntity<Map<String, List<ListingResponse>>> response = restTemplate.exchange(
                "/api/listings", HttpMethod.GET, null, new ParameterizedTypeReference<>() {});

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        List<ListingResponse> listings = response.getBody().get("listings");
        
        assertThat(listings).hasSize(2);
        assertThat(listings).extracting(ListingResponse::id)
                .containsExactlyInAnyOrder(publishedListing1Id.toString(), publishedListing2Id.toString());
    }

    // [LSS-TC02] 驗證城市條件篩選功能
    @Test
    void testSearchByCity() {
        ResponseEntity<Map<String, List<ListingResponse>>> response = restTemplate.exchange(
                "/api/listings?city=台北市", HttpMethod.GET, null, new ParameterizedTypeReference<>() {});

        List<ListingResponse> listings = response.getBody().get("listings");
        assertThat(listings).hasSize(1);
        assertThat(listings.get(0).city()).isEqualTo("台北市");
    }

    // [LSS-TC03] 驗證租金與房型條件篩選功能
    @Test
    void testSearchByRentAndPropertyType() {
        ResponseEntity<Map<String, List<ListingResponse>>> response = restTemplate.exchange(
                "/api/listings?minRent=500000&maxRent=1500000&propertyType=SUITE", 
                HttpMethod.GET, null, new ParameterizedTypeReference<>() {});

        List<ListingResponse> listings = response.getBody().get("listings");
        assertThat(listings).hasSize(1);
        assertThat(listings.get(0).title()).isEqualTo("新北板橋便宜雅房");
        assertThat(listings.get(0).rentCents()).isEqualTo(800000L); // 8000 * 100
    }

    // [LSS-TC04] 驗證關鍵字與設施條件搜尋功能
    @Test
    void testSearchByKeywordAndAmenities() {
        ResponseEntity<Map<String, List<ListingResponse>>> response = restTemplate.exchange(
                "/api/listings?keyword=信義&hasInternet=true&hasAC=true", 
                HttpMethod.GET, null, new ParameterizedTypeReference<>() {});

        List<ListingResponse> listings = response.getBody().get("listings");
        assertThat(listings).hasSize(1);
        assertThat(listings.get(0).title()).contains("信義");
    }

    // [LSS-TC05] 驗證分頁與排序功能
    @Test
    void testPaginationAndSorting() {
        ResponseEntity<Map<String, List<ListingResponse>>> response = restTemplate.exchange(
                "/api/listings?size=1&page=0&sort=RENT_ASC", 
                HttpMethod.GET, null, new ParameterizedTypeReference<>() {});

        List<ListingResponse> listings = response.getBody().get("listings");
        assertThat(listings).hasSize(1);
        assertThat(listings.get(0).rentCents()).isEqualTo(800000L);
    }

    // 額外補充：測試單筆查詢 (修正前方的 Bug)
    @Test
    void testGetListingById() {
        ResponseEntity<ListingResponse> response = restTemplate.getForEntity(
                "/api/listings/" + publishedListing1Id, ListingResponse.class);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().title()).isEqualTo("台北信義高級公寓");
    }

    @Test
    void testGetListingById_NotFound() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/listings/" + UUID.randomUUID(), String.class);

        assertThat(response.getStatusCode().is4xxClientError()).isTrue();
    }
}
