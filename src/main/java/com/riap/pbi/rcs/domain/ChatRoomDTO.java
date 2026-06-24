package com.riap.pbi.rcs.domain;

public class ChatRoomDTO {
    private String id;
    private String tenantId;
    private String landlordId;
    private String listingId;
    private String otherUserName;
    private String listingTitle;
    private boolean hasUnread;

    public ChatRoomDTO(String id, String tenantId, String landlordId, String listingId, String otherUserName, String listingTitle, boolean hasUnread) {
        this.id = id;
        this.tenantId = tenantId;
        this.landlordId = landlordId;
        this.listingId = listingId;
        this.otherUserName = otherUserName;
        this.listingTitle = listingTitle;
        this.hasUnread = hasUnread;
    }

    public String getId() { return id; }
    public String getTenantId() { return tenantId; }
    public String getLandlordId() { return landlordId; }
    public String getListingId() { return listingId; }
    public String getOtherUserName() { return otherUserName; }
    public String getListingTitle() { return listingTitle; }
    public boolean getHasUnread() { return hasUnread; }
    public boolean hasUnread() { return hasUnread; }
}
