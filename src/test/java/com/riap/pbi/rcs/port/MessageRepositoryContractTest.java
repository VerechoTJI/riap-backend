package com.riap.pbi.rcs.port;

import com.riap.pbi.rcs.domain.Message;
import org.junit.jupiter.api.Test;

import java.util.List;

import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Contract test for MessageRepository.
 * Any future real database implementation of MessageRepository must extend this class
 * and provide an instance of the repository to ensure all read/write logics are consistent.
 */
public abstract class MessageRepositoryContractTest {

    protected abstract MessageRepository getRepository();

    @Test
    void testMarkAsRead_ShouldNotMarkOwnMessages() {
        MessageRepository repository = getRepository();
        String chatRoomId = "room-contract-1";
        UUID senderId = UUID.randomUUID();
        
        // Sender sends a message
        repository.addMessageRecord(chatRoomId, senderId, "Hello World");
        
        // Sender calls markAsRead for themselves
        repository.markAsRead(chatRoomId, senderId);
        
        // Retrieve messages
        List<Message> messages = repository.findByChatRoomId(chatRoomId);
        assertEquals(1, messages.size());
        
        // Verify that the sender's own message was NOT marked as read
        assertFalse(messages.get(0).isRead(), "Sender's own message should not be marked as read by themselves in DB");
    }

    @Test
    void testMarkAsRead_ShouldMarkOtherUserMessages() {
        MessageRepository repository = getRepository();
        String chatRoomId = "room-contract-2";
        UUID senderId = UUID.randomUUID();
        UUID receiverId = UUID.randomUUID();
        
        // Sender sends a message
        repository.addMessageRecord(chatRoomId, senderId, "Hello World");
        
        // Receiver calls markAsRead
        repository.markAsRead(chatRoomId, receiverId);
        
        // Retrieve messages
        List<Message> messages = repository.findByChatRoomId(chatRoomId);
        assertEquals(1, messages.size());
        
        // Verify that the message was marked as read
        assertTrue(messages.get(0).isRead(), "Message should be marked as read when the receiver reads it in DB");
    }

    @Test
    void testHasUnreadMessages() {
        MessageRepository repository = getRepository();
        String chatRoomId = "room-unread-1";
        UUID user1 = UUID.randomUUID();
        UUID user2 = UUID.randomUUID();
        
        // user-1 sends message to user-2
        repository.addMessageRecord(chatRoomId, user1, "Msg 1");
        
        // user-2 should have unread messages
        assertTrue(repository.hasUnreadMessages(chatRoomId, user2));
        // user-1 should NOT have unread messages (they sent it)
        assertFalse(repository.hasUnreadMessages(chatRoomId, user1));
        
        // user-2 reads
        repository.markAsRead(chatRoomId, user2);
        assertFalse(repository.hasUnreadMessages(chatRoomId, user2));
    }

    @Test
    void testHasAnyUnreadMessages() {
        MessageRepository repository = getRepository();
        UUID user1 = UUID.randomUUID();
        UUID user2 = UUID.randomUUID();
        UUID user3 = UUID.randomUUID();
        
        // user-1 sends message to user-2 in room A
        repository.addMessageRecord("room-A", user1, "Hello");
        
        // user-2 should have unread in ANY room
        assertTrue(repository.hasAnyUnreadMessages(user2));
        assertFalse(repository.hasAnyUnreadMessages(user1));
        
        // user-2 reads room A
        repository.markAsRead("room-A", user2);
        assertFalse(repository.hasAnyUnreadMessages(user2));
        
        // user-3 sends message to user-2 in room B
        repository.addMessageRecord("room-B", user3, "Hi");
        assertTrue(repository.hasAnyUnreadMessages(user2));
    }
}
