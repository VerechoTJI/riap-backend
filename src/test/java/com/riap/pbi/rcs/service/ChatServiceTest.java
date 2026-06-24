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

import java.util.UUID;
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
        UUID tenantId = UUID.randomUUID();
        UUID landlordId = UUID.randomUUID();
        ChatRoom mockedRoom = ChatRoom.rehydrate("room-1", tenantId, landlordId, "list-1");
        when(chatRoomRepository.addChatSession(tenantId, landlordId, "list-1")).thenReturn(mockedRoom);

        ChatRoom result = chatService.createOrGetRoom(tenantId, landlordId, "list-1");
        assertNotNull(result);
        assertEquals("room-1", result.getId());
    }

    @Test
    void testSaveAndBroadcastMessage() {
        UUID tenantId = UUID.randomUUID();
        Message mockedMsg = Message.rehydrate("msg-1", "room-1", tenantId, "Hello", Instant.now(), false);
        when(messageRepository.addMessageRecord("room-1", tenantId, "Hello")).thenReturn(mockedMsg);

        Message result = chatService.saveAndBroadcastMessage("room-1", tenantId, "Hello");
        assertNotNull(result);
        assertEquals("msg-1", result.getId());

        verify(messageRepository).addMessageRecord("room-1", tenantId, "Hello");
        verify(messageBroadcaster).broadcastToRoom("room-1", mockedMsg);
    }

    @Test
    void testSendQuotedMessage() {
        UUID tenantId = UUID.randomUUID();
        UUID landlordId = UUID.randomUUID();
        ChatRoom mockedRoom = ChatRoom.rehydrate("room-1", tenantId, landlordId, "list-1");
        when(chatRoomRepository.addChatSession(tenantId, landlordId, "list-1")).thenReturn(mockedRoom);
        java.util.Map<String, Object> summaryMap = new java.util.HashMap<>();
        summaryMap.put("title", "Beautiful Apartment");
        when(lmsClient.getListingSummary("list-1")).thenReturn(summaryMap);

        Message mockedMsg = Message.rehydrate("msg-1", "room-1", tenantId, "Hi Landlord, I'm interested in: Beautiful Apartment", Instant.now(), false);
        when(messageRepository.addMessageRecord(eq("room-1"), eq(tenantId), anyString())).thenReturn(mockedMsg);

        String msgId = chatService.sendQuotedMessage(tenantId, landlordId, "list-1");
        assertEquals("msg-1", msgId);

        verify(messageBroadcaster).broadcastToRoom(eq("room-1"), any(Message.class));
    }

    @Test
    void testGetUserChatRooms() {
        UUID tenantId1 = UUID.randomUUID();
        UUID tenantId2 = UUID.randomUUID();
        UUID landlordId = UUID.randomUUID();
        ChatRoom room1 = ChatRoom.rehydrate("room-1", tenantId1, landlordId, "list-1");
        ChatRoom room2 = ChatRoom.rehydrate("room-2", tenantId2, landlordId, "list-2");
        when(chatRoomRepository.findByUserId(landlordId)).thenReturn(Arrays.asList(room1, room2));
        when(uasClient.getUserProfile(tenantId1.toString())).thenReturn("Alice");
        when(uasClient.getUserProfile(tenantId2.toString())).thenReturn("Bob");
        
        java.util.Map<String, Object> summaryMap1 = new java.util.HashMap<>();
        summaryMap1.put("title", "Listing 1");
        when(lmsClient.getListingSummary("list-1")).thenReturn(summaryMap1);
        
        java.util.Map<String, Object> summaryMap2 = new java.util.HashMap<>();
        summaryMap2.put("title", "Listing 2");
        when(lmsClient.getListingSummary("list-2")).thenReturn(summaryMap2);
        
        when(messageRepository.hasUnreadMessages("room-1", landlordId)).thenReturn(true);
        when(messageRepository.hasUnreadMessages("room-2", landlordId)).thenReturn(false);

        List<com.riap.pbi.rcs.domain.ChatRoomDTO> result = chatService.getUserChatRooms(landlordId);
        
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
        UUID userId = UUID.randomUUID();
        when(messageRepository.hasAnyUnreadMessages(userId)).thenReturn(true);
        assertTrue(chatService.hasGlobalUnread(userId));
        
        when(messageRepository.hasAnyUnreadMessages(userId)).thenReturn(false);
        assertFalse(chatService.hasGlobalUnread(userId));
    }
}
