package com.riap.pbi.rcs.port;

import com.riap.pbi.rcs.domain.Message;
import org.junit.jupiter.api.Test;

import java.util.List;

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
        String senderId = "user-1";
        
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
        String senderId = "user-1";
        String receiverId = "user-2";
        
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
        
        // user-1 sends message to user-2
        repository.addMessageRecord(chatRoomId, "user-1", "Msg 1");
        
        // user-2 should have unread messages
        assertTrue(repository.hasUnreadMessages(chatRoomId, "user-2"));
        // user-1 should NOT have unread messages (they sent it)
        assertFalse(repository.hasUnreadMessages(chatRoomId, "user-1"));
        
        // user-2 reads
        repository.markAsRead(chatRoomId, "user-2");
        assertFalse(repository.hasUnreadMessages(chatRoomId, "user-2"));
    }

    @Test
    void testHasAnyUnreadMessages() {
        MessageRepository repository = getRepository();
        
        // user-1 sends message to user-2 in room A
        repository.addMessageRecord("room-A", "user-1", "Hello");
        
        // user-2 should have unread in ANY room
        assertTrue(repository.hasAnyUnreadMessages("user-2"));
        assertFalse(repository.hasAnyUnreadMessages("user-1"));
        
        // user-2 reads room A
        repository.markAsRead("room-A", "user-2");
        assertFalse(repository.hasAnyUnreadMessages("user-2"));
        
        // user-3 sends message to user-2 in room B
        repository.addMessageRecord("room-B", "user-3", "Hi");
        assertTrue(repository.hasAnyUnreadMessages("user-2"));
    }
}
