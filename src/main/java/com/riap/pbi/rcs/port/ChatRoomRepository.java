package com.riap.pbi.rcs.port;

import com.riap.pbi.rcs.domain.ChatRoom;
import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository {
    ChatRoom addChatSession(String tenantId, String landlordId, String listingId);
    Optional<ChatRoom> findById(String id);
    List<ChatRoom> findByUserId(String userId);
}
