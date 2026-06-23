package com.riap.pbi.rcs.infrastructure.fake;

import com.riap.pbi.rcs.domain.Message;
import com.riap.pbi.rcs.port.MessageRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Repository
public class InMemoryMessageRepository implements MessageRepository {

    private final List<Message> messages = new CopyOnWriteArrayList<>();

    @Override
    public Message addMessageRecord(String chatRoomId, String senderUserId, String content) {
        Message msg = Message.create(chatRoomId, senderUserId, content);
        messages.add(msg);
        return msg;
    }

    @Override
    public List<Message> findByChatRoomId(String chatRoomId) {
        return messages.stream()
                .filter(msg -> msg.getChatRoomId().equals(chatRoomId))
                .sorted(Comparator.comparing(Message::getSentAt))
                .collect(Collectors.toList());
    }

    @Override
    public void markAsRead(String chatRoomId, String receiverUserId) {
        for (int i = 0; i < messages.size(); i++) {
            Message msg = messages.get(i);
            if (msg.getChatRoomId().equals(chatRoomId) && !msg.getSenderUserId().equals(receiverUserId) && !msg.isRead()) {
                Message updated = Message.rehydrate(msg.getId(), msg.getChatRoomId(), msg.getSenderUserId(), msg.getContent(), msg.getSentAt(), true);
                messages.set(i, updated);
            }
        }
    }
}
