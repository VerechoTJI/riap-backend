package com.riap.listing.infrastructure.rest;

import com.riap.listing.application.service.ListingService;
import com.riap.listing.domain.model.ListingEntity;
import com.riap.listing.domain.model.ListingStatus;
import com.riap.user.domain.model.UserRole;
import com.riap.user.security.AuthenticatedUser;
import com.riap.user.security.RequireRole;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/listings")
@RequiredArgsConstructor
public class ListingController {

    private final ListingService listingService;

    @PostMapping
    @RequireRole(UserRole.LANDLORD)
    public ResponseEntity<ListingEntity> publish(@RequestBody ListingEntity listing, @RequestAttribute(name = AuthenticatedUser.ATTRIBUTE) AuthenticatedUser user) {
        listing.setLandlordId(user.userId());
        return ResponseEntity.ok(listingService.publishListing(listing));
    }

    @PutMapping("/{id}")
    @RequireRole(UserRole.LANDLORD)
    public ResponseEntity<ListingEntity> update(@PathVariable UUID id, @RequestBody ListingEntity listing, @RequestAttribute(name = AuthenticatedUser.ATTRIBUTE) AuthenticatedUser user) {
        listing.setLandlordId(user.userId());
        // In a real application, we might want to check if the listing belongs to the user
        // but for now we'll just update it
        return ResponseEntity.ok(listingService.updateListing(id, listing));
    }

    @GetMapping("/pending")
    @RequireRole(UserRole.ADMIN)
    public ResponseEntity<List<ListingEntity>> getPending() {
        return ResponseEntity.ok(listingService.getPendingListings());
    }

    @GetMapping("/published")
    public ResponseEntity<List<ListingEntity>> getPublished() {
        return ResponseEntity.ok(listingService.getPublishedListings());
    }

    @GetMapping("/landlord/{landlordId}")
    @RequireRole({UserRole.LANDLORD, UserRole.ADMIN})
    public ResponseEntity<List<ListingEntity>> getByLandlord(@PathVariable UUID landlordId, @RequestAttribute(name = AuthenticatedUser.ATTRIBUTE) AuthenticatedUser user) {
        if (user.role() == UserRole.LANDLORD && !user.userId().equals(landlordId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(listingService.getListingsByLandlordId(landlordId));
    }

    @PatchMapping("/{id}/review")
    @RequireRole(UserRole.ADMIN)
    public ResponseEntity<ListingEntity> review(@PathVariable UUID id, @RequestBody ReviewRequest request) {
        return ResponseEntity.ok(listingService.reviewListing(id, request.getStatus(), request.getReason()));
    }

    @PatchMapping("/{id}/unpublish")
    @RequireRole({UserRole.LANDLORD, UserRole.ADMIN})
    public ResponseEntity<ListingEntity> unpublish(@PathVariable UUID id) {
        return ResponseEntity.ok(listingService.unpublishListing(id));
    }

    @PatchMapping("/{id}/resubmit")
    @RequireRole(UserRole.LANDLORD)
    public ResponseEntity<ListingEntity> resubmit(@PathVariable UUID id) {
        return ResponseEntity.ok(listingService.resubmitListing(id));
    }

    @PatchMapping("/bulk-unpublish/{landlordId}")
    @RequireRole({UserRole.LANDLORD, UserRole.ADMIN})
    public ResponseEntity<Void> bulkUnpublish(@PathVariable UUID landlordId, @RequestAttribute(name = AuthenticatedUser.ATTRIBUTE) AuthenticatedUser user) {
        if (user.role() == UserRole.LANDLORD && !user.userId().equals(landlordId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        listingService.unpublishAllByLandlord(landlordId);
        return ResponseEntity.ok().build();
    }

    @Data
    public static class ReviewRequest {
        private ListingStatus status;
        private String reason;
    }
}
