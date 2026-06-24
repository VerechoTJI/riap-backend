package com.riap.pbi.rcs.service;

import com.riap.pbi.rcs.domain.ChatRoom;
import com.riap.pbi.rcs.domain.Message;
import com.riap.pbi.rcs.port.ChatRoomRepository;
import com.riap.pbi.rcs.port.LmsClient;
import com.riap.pbi.rcs.port.MessageBroadcaster;
import com.riap.pbi.rcs.port.MessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ChatServiceTest {

    private ChatRoomRepository chatRoomRepository;
    private MessageRepository messageRepository;
    private LmsClient lmsClient;
    private com.riap.pbi.rcs.port.UasClient uasClient;
    private MessageBroadcaster messageBroadcaster;
    private ChatService chatService;

    @BeforeEach
    void setUp() {
        chatRoomRepository = mock(ChatRoomRepository.class);
        messageRepository = mock(MessageRepository.class);
        lmsClient = mock(LmsClient.class);
        uasClient = mock(com.riap.pbi.rcs.port.UasClient.class);
        messageBroadcaster = mock(MessageBroadcaster.class);
        chatService = new ChatService(chatRoomRepository, messageRepository, lmsClient, uasClient, messageBroadcaster);
    }

    @Test
    void testCreateOrGetRoom() {
        ChatRoom mockedRoom = ChatRoom.rehydrate("room-1", "tenant-1", "landlord-1", "list-1");
        when(chatRoomRepository.addChatSession("tenant-1", "landlord-1", "list-1")).thenReturn(mockedRoom);

        ChatRoom result = chatService.createOrGetRoom("tenant-1", "landlord-1", "list-1");
        assertNotNull(result);
        assertEquals("room-1", result.getId());
    }

    @Test
    void testSaveAndBroadcastMessage() {
        Message mockedMsg = Message.rehydrate("msg-1", "room-1", "tenant-1", "Hello", Instant.now(), false);
        when(messageRepository.addMessageRecord("room-1", "tenant-1", "Hello")).thenReturn(mockedMsg);

        Message result = chatService.saveAndBroadcastMessage("room-1", "tenant-1", "Hello");
        assertNotNull(result);
        assertEquals("msg-1", result.getId());

        verify(messageRepository).addMessageRecord("room-1", "tenant-1", "Hello");
        verify(messageBroadcaster).broadcastToRoom("room-1", mockedMsg);
    }

    @Test
    void testSendQuotedMessage() {
        ChatRoom mockedRoom = ChatRoom.rehydrate("room-1", "tenant-1", "landlord-1", "list-1");
        when(chatRoomRepository.addChatSession("tenant-1", "landlord-1", "list-1")).thenReturn(mockedRoom);
        when(lmsClient.getListingSummary("list-1")).thenReturn("Beautiful Apartment");

        Message mockedMsg = Message.rehydrate("msg-1", "room-1", "tenant-1", "Hi Landlord, I'm interested in: Beautiful Apartment", Instant.now(), false);
        when(messageRepository.addMessageRecord(eq("room-1"), eq("tenant-1"), anyString())).thenReturn(mockedMsg);

        String msgId = chatService.sendQuotedMessage("tenant-1", "landlord-1", "list-1");
        assertEquals("msg-1", msgId);

        verify(messageBroadcaster).broadcastToRoom(eq("room-1"), any(Message.class));
    }

    @Test
    void testGetUserChatRooms() {
        ChatRoom room1 = ChatRoom.rehydrate("room-1", "tenant-1", "landlord-1", "list-1");
        ChatRoom room2 = ChatRoom.rehydrate("room-2", "tenant-2", "landlord-1", "list-2");
        when(chatRoomRepository.findByUserId("landlord-1")).thenReturn(Arrays.asList(room1, room2));
        when(uasClient.getUserProfile("tenant-1")).thenReturn("Alice");
        when(uasClient.getUserProfile("tenant-2")).thenReturn("Bob");
        when(lmsClient.getListingSummary("list-1")).thenReturn("Listing 1");
        when(lmsClient.getListingSummary("list-2")).thenReturn("Listing 2");
        
        when(messageRepository.hasUnreadMessages("room-1", "landlord-1")).thenReturn(true);
        when(messageRepository.hasUnreadMessages("room-2", "landlord-1")).thenReturn(false);

        List<com.riap.pbi.rcs.domain.ChatRoomDTO> result = chatService.getUserChatRooms("landlord-1");
        
        assertEquals(2, result.size());
        
        assertEquals("room-1", result.get(0).getId());
        assertEquals("Alice", result.get(0).getOtherUserName());
        assertTrue(result.get(0).hasUnread());
        
        assertEquals("room-2", result.get(1).getId());
        assertEquals("Bob", result.get(1).getOtherUserName());
        assertFalse(result.get(1).hasUnread());
    }

    @Test
    void testHasGlobalUnread() {
        when(messageRepository.hasAnyUnreadMessages("user-1")).thenReturn(true);
        assertTrue(chatService.hasGlobalUnread("user-1"));
        
        when(messageRepository.hasAnyUnreadMessages("user-1")).thenReturn(false);
        assertFalse(chatService.hasGlobalUnread("user-1"));
    }
}
