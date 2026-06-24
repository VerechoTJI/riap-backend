package com.riap.pbi.rcs.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ChatRoomTest {

    @Test
    void testCreateChatRoom() {
        ChatRoom chatRoom = ChatRoom.create("tenant-1", "landlord-2", "listing-3");
        assertNotNull(chatRoom.getId());
        assertEquals("tenant-1", chatRoom.getTenantId());
        assertEquals("landlord-2", chatRoom.getLandlordId());
        assertEquals("listing-3", chatRoom.getListingId());
    }

    @Test
    void testCreateChatRoom_NullListingId_Allowed() {
        ChatRoom chatRoom = ChatRoom.create("tenant-1", "landlord-2", null);
        assertNotNull(chatRoom.getId());
        assertNull(chatRoom.getListingId());
    }

    @Test
    void testRehydrate() {
        ChatRoom chatRoom = ChatRoom.rehydrate("room-1", "tenant-1", "landlord-2", "listing-3");
        assertEquals("room-1", chatRoom.getId());
        assertEquals("tenant-1", chatRoom.getTenantId());
        assertEquals("landlord-2", chatRoom.getLandlordId());
        assertEquals("listing-3", chatRoom.getListingId());
    }
}
