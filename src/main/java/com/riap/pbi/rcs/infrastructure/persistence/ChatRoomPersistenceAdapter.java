package com.riap.pbi.rcs.infrastructure.persistence;

import com.riap.pbi.rcs.domain.ChatRoom;
import com.riap.pbi.rcs.port.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ChatRoomPersistenceAdapter implements ChatRoomRepository {

    private final JpaChatRoomRepository jpaRepository;

    @Override
    public ChatRoom addChatSession(String tenantId, String landlordId, String listingId) {
        ChatRoom chatRoom = ChatRoom.create(tenantId, landlordId, listingId);
        
        ChatRoomEntity entity = ChatRoomEntity.builder()
                .id(chatRoom.getId())
                .tenantId(chatRoom.getTenantId())
                .landlordId(chatRoom.getLandlordId())
                .listingId(chatRoom.getListingId())
                .build();
                
        jpaRepository.save(entity);
        return chatRoom;
    }

    @Override
    public Optional<ChatRoom> findById(String id) {
        return jpaRepository.findById(id)
                .map(this::mapToDomain);
    }

    @Override
    public List<ChatRoom> findByUserId(String userId) {
        return jpaRepository.findByTenantIdOrLandlordId(userId, userId).stream()
                .map(this::mapToDomain)
                .collect(Collectors.toList());
    }

    private ChatRoom mapToDomain(ChatRoomEntity entity) {
        return ChatRoom.rehydrate(
                entity.getId(),
                entity.getTenantId(),
                entity.getLandlordId(),
                entity.getListingId()
        );
    }
}
