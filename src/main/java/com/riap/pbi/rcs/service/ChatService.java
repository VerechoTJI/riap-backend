package com.riap.pbi.rcs.service;

import com.riap.pbi.rcs.domain.ChatRoom;
import com.riap.pbi.rcs.domain.Message;
import com.riap.pbi.rcs.port.ChatRoomRepository;
import com.riap.pbi.rcs.port.LmsClient;
import com.riap.pbi.rcs.port.MessageBroadcaster;
import com.riap.pbi.rcs.port.MessageRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final MessageRepository messageRepository;
    private final LmsClient lmsClient;
    private final MessageBroadcaster messageBroadcaster;

    public ChatService(ChatRoomRepository chatRoomRepository,
                       MessageRepository messageRepository,
                       LmsClient lmsClient,
                       MessageBroadcaster messageBroadcaster) {
        this.chatRoomRepository = chatRoomRepository;
        this.messageRepository = messageRepository;
        this.lmsClient = lmsClient;
        this.messageBroadcaster = messageBroadcaster;
    }

    public ChatRoom createOrGetRoom(String tenantId, String landlordId, String listingId) {
        // SDD implies it first checks history, but our current mock repository handles creation/fetching
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

    public List<ChatRoom> getUserChatRooms(String userId) {
        return chatRoomRepository.findByUserId(userId);
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
