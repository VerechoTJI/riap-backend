package com.riap.pbi.rcs.domain;

import java.util.UUID;

public class ChatRoomDTO {
    private String id;
    private UUID tenantId;
    private UUID landlordId;
    private String listingId;
    private String otherUserName;
    private String listingTitle;
    private boolean hasUnread;

    public ChatRoomDTO(String id, UUID tenantId, UUID landlordId, String listingId, String otherUserName, String listingTitle, boolean hasUnread) {
        this.id = id;
        this.tenantId = tenantId;
        this.landlordId = landlordId;
        this.listingId = listingId;
        this.otherUserName = otherUserName;
        this.listingTitle = listingTitle;
        this.hasUnread = hasUnread;
    }

    public String getId() { return id; }
    public UUID getTenantId() { return tenantId; }
    public UUID getLandlordId() { return landlordId; }
    public String getListingId() { return listingId; }
    public String getOtherUserName() { return otherUserName; }
    public String getListingTitle() { return listingTitle; }
    public boolean getHasUnread() { return hasUnread; }
    public boolean hasUnread() { return hasUnread; }
}
