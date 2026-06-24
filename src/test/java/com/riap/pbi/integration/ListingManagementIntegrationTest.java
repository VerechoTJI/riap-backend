package com.riap.pbi.integration;

import com.riap.listing.domain.model.FeeDisclosure;
import com.riap.listing.domain.model.ListingEntity;
import com.riap.listing.domain.model.ListingStatus;
import com.riap.listing.domain.repository.ListingRepository;
import com.riap.listing.infrastructure.rest.ListingController.ReviewRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class ListingManagementIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ListingRepository listingRepository;

    @BeforeEach
    void setup() {
        listingRepository.deleteAll();
    }

    private FeeDisclosure createValidFeeDisclosure() {
        FeeDisclosure fee = new FeeDisclosure();
        fee.setRent(BigDecimal.valueOf(15000));
        fee.setDeposit(BigDecimal.valueOf(30000));
        fee.setManagementFee(BigDecimal.valueOf(1000));
        fee.setWaterElectricityRules("台水台電");
        return fee;
    }

    // [LMS-TC01] 驗證房源刊登與強制費用揭露功能
    @Test
    void testPublishListing_Success() {
        ListingEntity newListing = ListingEntity.builder()
                .title("新房源")
                .landlordId("user-123")
                .feeDisclosure(createValidFeeDisclosure())
                .build();

        ResponseEntity<ListingEntity> response = restTemplate.postForEntity("/api/listings", newListing, ListingEntity.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(ListingStatus.PENDING);
        assertThat(response.getBody().getId()).isNotNull();

        // Verify in DB
        ListingEntity saved = listingRepository.findById(response.getBody().getId()).orElseThrow();
        assertThat(saved.getStatus()).isEqualTo(ListingStatus.PENDING);
        assertThat(saved.getFeeDisclosure().getRent().compareTo(BigDecimal.valueOf(15000))).isEqualTo(0);
    }

    // [LMS-TC01] 驗證房源刊登與強制費用揭露功能 - 缺少費用
    @Test
    void testPublishListing_MissingFeeDisclosure_Returns500() {
        ListingEntity newListing = ListingEntity.builder()
                .title("新房源")
                .landlordId("user-123")
                .build();

        // Service throws IllegalArgumentException. In default Spring Boot without ExceptionHandler, it becomes 500.
        ResponseEntity<String> response = restTemplate.postForEntity("/api/listings", newListing, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // [LMS-TC02] 驗證房源審核與退回機制
    @Test
    void testReviewListing_Approve() {
        ListingEntity listing = listingRepository.save(ListingEntity.builder()
                .title("Pending Listing")
                .status(ListingStatus.PENDING)
                .build());

        ReviewRequest request = new ReviewRequest();
        request.setStatus(ListingStatus.PUBLISHED);

        ResponseEntity<ListingEntity> response = restTemplate.exchange(
                "/api/listings/" + listing.getId() + "/review",
                HttpMethod.PATCH,
                new HttpEntity<>(request),
                ListingEntity.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getStatus()).isEqualTo(ListingStatus.PUBLISHED);

        ListingEntity saved = listingRepository.findById(listing.getId()).orElseThrow();
        assertThat(saved.getStatus()).isEqualTo(ListingStatus.PUBLISHED);
    }

    // [LMS-TC02] 驗證房源審核與退回機制
    @Test
    void testReviewListing_ReturnWithReason() {
        ListingEntity listing = listingRepository.save(ListingEntity.builder()
                .title("Pending Listing")
                .status(ListingStatus.PENDING)
                .build());

        ReviewRequest request = new ReviewRequest();
        request.setStatus(ListingStatus.RETURNED);
        request.setReason("照片不清楚");

        ResponseEntity<ListingEntity> response = restTemplate.exchange(
                "/api/listings/" + listing.getId() + "/review",
                HttpMethod.PATCH,
                new HttpEntity<>(request),
                ListingEntity.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getStatus()).isEqualTo(ListingStatus.RETURNED);
        assertThat(response.getBody().getReturnReason()).isEqualTo("照片不清楚");
    }

    // [LMS-TC03] 驗證單一房源下架功能
    @Test
    void testUnpublishSingleListing() {
        ListingEntity listing = listingRepository.save(ListingEntity.builder()
                .title("L1")
                .landlordId("landlord-1")
                .status(ListingStatus.PUBLISHED)
                .build());

        ResponseEntity<ListingEntity> response = restTemplate.exchange(
                "/api/listings/" + listing.getId() + "/unpublish",
                HttpMethod.PATCH,
                null,
                ListingEntity.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getStatus()).isEqualTo(ListingStatus.PRIVATE);

        ListingEntity saved = listingRepository.findById(listing.getId()).orElseThrow();
        assertThat(saved.getStatus()).isEqualTo(ListingStatus.PRIVATE);
    }

    // [LMS-TC05] 驗證一鍵全部下架功能
    @Test
    void testBulkUnpublish() {
        String landlordId = "landlord-x";
        listingRepository.save(ListingEntity.builder().title("L1").landlordId(landlordId).status(ListingStatus.PUBLISHED).build());
        listingRepository.save(ListingEntity.builder().title("L2").landlordId(landlordId).status(ListingStatus.PUBLISHED).build());
        listingRepository.save(ListingEntity.builder().title("L3").landlordId("other").status(ListingStatus.PUBLISHED).build());

        ResponseEntity<Void> response = restTemplate.exchange(
                "/api/listings/bulk-unpublish/" + landlordId,
                HttpMethod.PATCH,
                null,
                Void.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        List<ListingEntity> myAft = listingRepository.findByLandlordId(landlordId);
        assertThat(myAft).allMatch(l -> l.getStatus() == ListingStatus.PRIVATE);

        List<ListingEntity> otherAft = listingRepository.findByLandlordId("other");
        assertThat(otherAft).allMatch(l -> l.getStatus() == ListingStatus.PUBLISHED);
    }

    // [LMS-TC04] 驗證房源重新送審功能
    @Test
    void testResubmitListing() {
        ListingEntity listing = listingRepository.save(ListingEntity.builder()
                .title("Returned Listing")
                .status(ListingStatus.RETURNED)
                .returnReason("Fix photo")
                .build());

        ResponseEntity<ListingEntity> response = restTemplate.exchange(
                "/api/listings/" + listing.getId() + "/resubmit",
                HttpMethod.PATCH,
                null,
                ListingEntity.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getStatus()).isEqualTo(ListingStatus.PENDING);
        assertThat(response.getBody().getReturnReason()).isNull(); // Should clear reason

        ListingEntity saved = listingRepository.findById(listing.getId()).orElseThrow();
        assertThat(saved.getStatus()).isEqualTo(ListingStatus.PENDING);
        assertThat(saved.getReturnReason()).isNull();
    }
}
