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

import com.riap.user.domain.model.UserRole;
import com.riap.user.security.JwtService;

import java.math.BigDecimal;
import java.util.Collections;
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

    @Autowired
    private JwtService jwtService;

    private UUID adminId;

    @BeforeEach
    void setup() {
        listingRepository.deleteAll();
        
        // Generate an ADMIN token to bypass all role checks in these tests easily, or we can just change tokens per request.
        // Wait, for LMS-TC01 we need landlord, and LMS-TC02 we need admin.
        // Let's just create a generic interceptor that uses a field `currentToken`.
        restTemplate.getRestTemplate().setInterceptors(List.of((request, body, execution) -> {
            if (currentToken != null) {
                request.getHeaders().add("Authorization", "Bearer " + currentToken);
            }
            return execution.execute(request, body);
        }));
    }

    private String currentToken;

    private void asRole(UserRole role) {
        this.currentToken = jwtService.generateToken(UUID.randomUUID(), role);
    }
    
    private void asUser(UUID userId, UserRole role) {
        this.currentToken = jwtService.generateToken(userId, role);
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
        UUID landlordId = UUID.randomUUID();
        asUser(landlordId, UserRole.LANDLORD);
        ListingEntity newListing = ListingEntity.builder()
                .title("新房源")
                .landlordId(landlordId)
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
        UUID landlordId = UUID.randomUUID();
        asUser(landlordId, UserRole.LANDLORD);
        ListingEntity newListing = ListingEntity.builder()
                .title("新房源")
                .landlordId(landlordId)
                .build();

        // Service throws IllegalArgumentException. In default Spring Boot without ExceptionHandler, it becomes 500.
        ResponseEntity<String> response = restTemplate.postForEntity("/api/listings", newListing, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // [LMS-TC02] 驗證房源審核與退回機制
    @Test
    void testReviewListing_Approve() {
        asRole(UserRole.ADMIN);
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
        asRole(UserRole.ADMIN);
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
        UUID landlordId = UUID.randomUUID();
        asUser(landlordId, UserRole.LANDLORD);
        ListingEntity listing = listingRepository.save(ListingEntity.builder()
                .title("L1")
                .landlordId(landlordId)
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
        UUID landlordId = UUID.randomUUID();
        asUser(landlordId, UserRole.LANDLORD);
        UUID otherId = UUID.randomUUID();
        listingRepository.save(ListingEntity.builder().title("L1").landlordId(landlordId).status(ListingStatus.PUBLISHED).build());
        listingRepository.save(ListingEntity.builder().title("L2").landlordId(landlordId).status(ListingStatus.PUBLISHED).build());
        listingRepository.save(ListingEntity.builder().title("L3").landlordId(otherId).status(ListingStatus.PUBLISHED).build());

        ResponseEntity<Void> response = restTemplate.exchange(
                "/api/listings/bulk-unpublish/" + landlordId,
                HttpMethod.PATCH,
                null,
                Void.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        List<ListingEntity> myAft = listingRepository.findByLandlordId(landlordId);
        assertThat(myAft).allMatch(l -> l.getStatus() == ListingStatus.PRIVATE);

        List<ListingEntity> otherAft = listingRepository.findByLandlordId(otherId);
        assertThat(otherAft).allMatch(l -> l.getStatus() == ListingStatus.PUBLISHED);
    }

    // [LMS-TC04] 驗證房源重新送審功能
    @Test
    void testResubmitListing() {
        UUID landlordId = UUID.randomUUID();
        asUser(landlordId, UserRole.LANDLORD);
        ListingEntity listing = listingRepository.save(ListingEntity.builder()
                .title("Returned Listing")
                .landlordId(landlordId)
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
