package com.riap.pbi.rcs.domain;

import org.junit.jupiter.api.Test;
import java.time.Instant;
import static org.junit.jupiter.api.Assertions.*;

class MessageTest {

    @Test
    void testCreateMessage() {
        Message message = Message.create("room-1", "user-1", "Hello World");
        assertNotNull(message.getId());
        assertEquals("room-1", message.getChatRoomId());
        assertEquals("user-1", message.getSenderUserId());
        assertEquals("Hello World", message.getContent());
        assertNotNull(message.getSentAt());
        assertFalse(message.isRead());
    }

    @Test
    void testMarkAsRead() {
        Message message = Message.create("room-1", "user-1", "Hello");
        assertFalse(message.isRead());
        message.markAsRead();
        assertTrue(message.isRead());
    }

    @Test
    void testRehydrate() {
        Instant now = Instant.now();
        Message message = Message.rehydrate("msg-1", "room-1", "user-1", "Hi", now, true);
        assertEquals("msg-1", message.getId());
        assertEquals("room-1", message.getChatRoomId());
        assertEquals("user-1", message.getSenderUserId());
        assertEquals("Hi", message.getContent());
        assertEquals(now, message.getSentAt());
        assertTrue(message.isRead());
    }
}
