package com.riap.pbi.rcs.domain;

import java.util.UUID;

public final class ChatRoom {
    private final String id;
    private final String tenantId;
    private final String landlordId;
    private final String listingId;

    private ChatRoom(String id, String tenantId, String landlordId, String listingId) {
        this.id = id;
        this.tenantId = tenantId;
        this.landlordId = landlordId;
        this.listingId = listingId;
    }

    public static ChatRoom create(String tenantId, String landlordId, String listingId) {
        return new ChatRoom(UUID.randomUUID().toString(), tenantId, landlordId, listingId);
    }

    public static ChatRoom rehydrate(String id, String tenantId, String landlordId, String listingId) {
        return new ChatRoom(id, tenantId, landlordId, listingId);
    }

    public String getId() {
        return id;
    }

    public String getTenantId() {
        return tenantId;
    }

    public String getLandlordId() {
        return landlordId;
    }

    public String getListingId() {
        return listingId;
    }
}
