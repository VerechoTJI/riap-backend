package com.riap.pbi.rcs.domain;

import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class ChatRoomTest {

    @Test
    void testCreateChatRoom() {
        UUID tenantId = UUID.randomUUID();
        UUID landlordId = UUID.randomUUID();
        ChatRoom chatRoom = ChatRoom.create(tenantId, landlordId, "listing-3");
        assertNotNull(chatRoom.getId());
        assertEquals(tenantId, chatRoom.getTenantId());
        assertEquals(landlordId, chatRoom.getLandlordId());
        assertEquals("listing-3", chatRoom.getListingId());
    }

    @Test
    void testCreateChatRoom_NullListingId_Allowed() {
        UUID tenantId = UUID.randomUUID();
        UUID landlordId = UUID.randomUUID();
        ChatRoom chatRoom = ChatRoom.create(tenantId, landlordId, null);
        assertNotNull(chatRoom.getId());
        assertNull(chatRoom.getListingId());
    }

    @Test
    void testRehydrate() {
        UUID tenantId = UUID.randomUUID();
        UUID landlordId = UUID.randomUUID();
        ChatRoom chatRoom = ChatRoom.rehydrate("room-1", tenantId, landlordId, "listing-3");
        assertEquals("room-1", chatRoom.getId());
        assertEquals(tenantId, chatRoom.getTenantId());
        assertEquals(landlordId, chatRoom.getLandlordId());
        assertEquals("listing-3", chatRoom.getListingId());
    }
}
