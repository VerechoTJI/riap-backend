package com.riap.pbi.rcs.port;

import com.riap.pbi.rcs.domain.Message;
import java.util.List;

public interface MessageRepository {
    Message addMessageRecord(String chatRoomId, String senderUserId, String content);
    List<Message> findByChatRoomId(String chatRoomId);
    void markAsRead(String chatRoomId, String recipientId);
    boolean hasUnreadMessages(String chatRoomId, String userId);
    boolean hasAnyUnreadMessages(String userId);
}
