package com.riap.pbi.rcs.service;

import com.riap.pbi.rcs.domain.ChatRoom;
import com.riap.pbi.rcs.domain.ChatRoomDTO;
import com.riap.pbi.rcs.domain.Message;
import com.riap.pbi.rcs.port.ChatRoomRepository;
import com.riap.pbi.rcs.port.LmsClient;
import com.riap.pbi.rcs.port.MessageBroadcaster;
import com.riap.pbi.rcs.port.MessageRepository;
import com.riap.pbi.rcs.port.UasClient;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final MessageRepository messageRepository;
    private final LmsClient lmsClient;
    private final UasClient uasClient;
    private final MessageBroadcaster messageBroadcaster;

    public ChatService(ChatRoomRepository chatRoomRepository,
                       MessageRepository messageRepository,
                       LmsClient lmsClient,
                       UasClient uasClient,
                       MessageBroadcaster messageBroadcaster) {
        this.chatRoomRepository = chatRoomRepository;
        this.messageRepository = messageRepository;
        this.lmsClient = lmsClient;
        this.uasClient = uasClient;
        this.messageBroadcaster = messageBroadcaster;
    }

    public ChatRoom createOrGetRoom(String tenantId, String landlordId, String listingId) {
        return chatRoomRepository.addChatSession(tenantId, landlordId, listingId);
    }

    public Message saveAndBroadcastMessage(String chatRoomId, String senderUserId, String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Message content cannot be empty.");
        }
        Message msg = messageRepository.addMessageRecord(chatRoomId, senderUserId, content);
        messageBroadcaster.broadcastToRoom(chatRoomId, msg);
        return msg;
    }

    public String sendQuotedMessage(String tenantId, String landlordId, String listingId) {
        ChatRoom room = createOrGetRoom(tenantId, landlordId, listingId);
        String summary = lmsClient.getListingSummary(listingId);
        Message quoteMsg = Message.createQuoteMessage(room.getId(), tenantId, summary);
        
        Message saved = messageRepository.addMessageRecord(room.getId(), tenantId, quoteMsg.getContent());
        messageBroadcaster.broadcastToRoom(room.getId(), saved);
        return saved.getId();
    }

    public List<ChatRoomDTO> getUserChatRooms(String userId) {
        List<ChatRoom> rooms = chatRoomRepository.findByUserId(userId);
        return rooms.stream().map(room -> {
            String otherUserId = userId.equals(room.getLandlordId()) ? room.getTenantId() : room.getLandlordId();
            String otherUserName = uasClient.getUserProfile(otherUserId);
            String listingTitle = lmsClient.getListingSummary(room.getListingId());
            boolean hasUnread = messageRepository.hasUnreadMessages(room.getId(), userId);
            
            return new ChatRoomDTO(
                    room.getId(),
                    room.getTenantId(),
                    room.getLandlordId(),
                    room.getListingId(),
                    otherUserName != null ? otherUserName : "對方",
                    listingTitle != null ? listingTitle : "租屋對話",
                    hasUnread
            );
        }).collect(Collectors.toList());
    }

    public boolean hasGlobalUnread(String userId) {
        return messageRepository.hasAnyUnreadMessages(userId);
    }

    public List<Message> getMessages(String chatRoomId, String userId) {
        // userId check could be added
        return messageRepository.findByChatRoomId(chatRoomId);
    }

    public void markMessagesAsRead(String chatRoomId, String receiverId) {
        messageRepository.markAsRead(chatRoomId, receiverId);
        messageBroadcaster.notifyReadReceipt(chatRoomId, receiverId);
    }
}
