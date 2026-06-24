package com.riap.pbi.rcs.infrastructure.fake;

import com.riap.pbi.rcs.domain.ChatRoom;
import com.riap.pbi.rcs.port.ChatRoomRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class InMemoryChatRoomRepository implements ChatRoomRepository {

    private final Map<String, ChatRoom> chatRooms = new ConcurrentHashMap<>();

    @Override
    public ChatRoom addChatSession(String tenantId, String landlordId, String listingId) {
        // Find existing
        for (ChatRoom room : chatRooms.values()) {
            if (room.getTenantId().equals(tenantId) && room.getLandlordId().equals(landlordId) && 
                (listingId == null || listingId.equals(room.getListingId()))) {
                return room;
            }
        }
        
        String newId = UUID.randomUUID().toString();
        ChatRoom newRoom = ChatRoom.rehydrate(newId, tenantId, landlordId, listingId);
        chatRooms.put(newId, newRoom);
        return newRoom;
    }

    @Override
    public Optional<ChatRoom> findById(String id) {
        return Optional.ofNullable(chatRooms.get(id));
    }

    @Override
    public List<ChatRoom> findByUserId(String userId) {
        return chatRooms.values().stream()
                .filter(room -> room.getTenantId().equals(userId) || room.getLandlordId().equals(userId))
                .collect(Collectors.toList());
    }
}
