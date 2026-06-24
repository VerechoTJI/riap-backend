package com.riap.pbi.rcs.infrastructure.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "chat_messages")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageEntity {

    @Id
    private String id;
    private String chatRoomId;
    private UUID senderUserId;
    private String content;
    private Instant sentAt;
    private boolean isRead;
}
