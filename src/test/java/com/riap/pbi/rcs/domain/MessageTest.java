package com.riap.pbi.rcs.domain;

import org.junit.jupiter.api.Test;
import java.time.Instant;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class MessageTest {

    @Test
    void testCreateMessage() {
        UUID userId = UUID.randomUUID();
        Message message = Message.create("room-1", userId, "Hello World");
        assertNotNull(message.getId());
        assertEquals("room-1", message.getChatRoomId());
        assertEquals(userId, message.getSenderUserId());
        assertEquals("Hello World", message.getContent());
        assertNotNull(message.getSentAt());
        assertFalse(message.isRead());
    }

    @Test
    void testMarkAsRead() {
        UUID userId = UUID.randomUUID();
        Message message = Message.create("room-1", userId, "Hello");
        assertFalse(message.isRead());
        message.markAsRead();
        assertTrue(message.isRead());
    }

    @Test
    void testRehydrate() {
        Instant now = Instant.now();
        UUID userId = UUID.randomUUID();
        Message message = Message.rehydrate("msg-1", "room-1", userId, "Hi", now, true);
        assertEquals("msg-1", message.getId());
        assertEquals("room-1", message.getChatRoomId());
        assertEquals(userId, message.getSenderUserId());
        assertEquals("Hi", message.getContent());
        assertEquals(now, message.getSentAt());
        assertTrue(message.isRead());
    }
}
