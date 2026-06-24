package com.riap.pbi.rcs.domain;

import java.util.UUID;

public final class ChatRoom {
    private final String id;
    private final UUID tenantId;
    private final UUID landlordId;
    private final String listingId;

    private ChatRoom(String id, UUID tenantId, UUID landlordId, String listingId) {
        this.id = id;
        this.tenantId = tenantId;
        this.landlordId = landlordId;
        this.listingId = listingId;
    }

    public static ChatRoom create(UUID tenantId, UUID landlordId, String listingId) {
        return new ChatRoom(UUID.randomUUID().toString(), tenantId, landlordId, listingId);
    }

    public static ChatRoom rehydrate(String id, UUID tenantId, UUID landlordId, String listingId) {
        return new ChatRoom(id, tenantId, landlordId, listingId);
    }

    public String getId() {
        return id;
    }

    public UUID getTenantId() {
        return tenantId;
    }

    public UUID getLandlordId() {
        return landlordId;
    }

    public String getListingId() {
        return listingId;
    }
}
