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
    private MessageBroadcaster messageBroadcaster;
    private ChatService chatService;

    @BeforeEach
    void setUp() {
        chatRoomRepository = mock(ChatRoomRepository.class);
        messageRepository = mock(MessageRepository.class);
        lmsClient = mock(LmsClient.class);
        messageBroadcaster = mock(MessageBroadcaster.class);
        chatService = new ChatService(chatRoomRepository, messageRepository, lmsClient, messageBroadcaster);
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
}
