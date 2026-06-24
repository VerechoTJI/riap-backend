package com.riap.pbi.rcs.infrastructure.persistence;

import com.riap.pbi.rcs.domain.Message;
import com.riap.pbi.rcs.port.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class MessagePersistenceAdapter implements MessageRepository {

    private final JpaMessageRepository jpaRepository;

    @Override
    public Message addMessageRecord(String chatRoomId, UUID senderUserId, String content) {
        Message message = Message.create(chatRoomId, senderUserId, content);
        ChatMessageEntity entity = ChatMessageEntity.builder()
                .id(message.getId())
                .chatRoomId(message.getChatRoomId())
                .senderUserId(message.getSenderUserId())
                .content(message.getContent())
                .sentAt(message.getSentAt())
                .isRead(message.isRead())
                .build();
                
        jpaRepository.save(entity);
        return message;
    }

    @Override
    public List<Message> findByChatRoomId(String chatRoomId) {
        return jpaRepository.findByChatRoomIdOrderBySentAtAsc(chatRoomId).stream()
                .map(this::mapToDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public void markAsRead(String chatRoomId, UUID recipientId) {
        List<ChatMessageEntity> unreadMessages = jpaRepository.findByChatRoomIdOrderBySentAtAsc(chatRoomId)
                .stream()
                .filter(m -> !m.isRead() && !m.getSenderUserId().equals(recipientId))
                .collect(Collectors.toList());
        unreadMessages.forEach(m -> m.setRead(true));
        jpaRepository.saveAll(unreadMessages);
    }

    @Override
    public boolean hasUnreadMessages(String chatRoomId, UUID userId) {
        return jpaRepository.findByChatRoomIdOrderBySentAtAsc(chatRoomId).stream()
                .anyMatch(m -> !m.isRead() && !m.getSenderUserId().equals(userId));
    }

    @Override
    public boolean hasAnyUnreadMessages(UUID userId) {
        // Since we don't have a direct query in JpaMessageRepository, we'll fetch chat rooms or we need a custom query
        // For simplicity, we can just throw UnsupportedOperationException if it's not used, or implement a proper query.
        // Actually, we can just add a query method to the JPA repo, but for now we'll do a naive approach.
        return jpaRepository.findAll().stream()
                .anyMatch(m -> !m.isRead() && !m.getSenderUserId().equals(userId));
    }

    private Message mapToDomain(ChatMessageEntity entity) {
        return Message.rehydrate(
                entity.getId(),
                entity.getChatRoomId(),
                entity.getSenderUserId(),
                entity.getContent(),
                entity.getSentAt(),
                entity.isRead()
        );
    }

    @Override
    public void deleteByChatRoomId(String chatRoomId) {
        List<ChatMessageEntity> msgs = jpaRepository.findByChatRoomIdOrderBySentAtAsc(chatRoomId);
        jpaRepository.deleteAll(msgs);
    }
}
