package com.riap.pbi.rcs.port;

import com.riap.pbi.rcs.domain.ChatRoom;
import java.util.List;
import java.util.Optional;

import java.util.UUID;

public interface ChatRoomRepository {
    ChatRoom addChatSession(UUID tenantId, UUID landlordId, String listingId);
    Optional<ChatRoom> findById(String id);
    List<ChatRoom> findByUserId(UUID userId);
    void deleteByUserId(UUID userId);
}
