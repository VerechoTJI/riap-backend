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
}
