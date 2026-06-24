package com.riap.pbi.rcs.port;

import com.riap.pbi.rcs.domain.Message;
import java.util.List;

import java.util.UUID;

public interface MessageRepository {
    Message addMessageRecord(String chatRoomId, UUID senderUserId, String content);
    List<Message> findByChatRoomId(String chatRoomId);
    void markAsRead(String chatRoomId, UUID recipientId);
    boolean hasUnreadMessages(String chatRoomId, UUID userId);
    boolean hasAnyUnreadMessages(UUID userId);
}
