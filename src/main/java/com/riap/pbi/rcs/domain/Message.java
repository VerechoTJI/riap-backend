package com.riap.pbi.rcs.domain;

import java.time.Instant;
import java.util.UUID;

public final class Message {
    private final String id;
    private final String chatRoomId;
    private final String senderUserId;
    private final String content;
    private final Instant sentAt;
    private boolean isRead;

    private Message(String id, String chatRoomId, String senderUserId, String content, Instant sentAt, boolean isRead) {
        this.id = id;
        this.chatRoomId = chatRoomId;
        this.senderUserId = senderUserId;
        this.content = content;
        this.sentAt = sentAt;
        this.isRead = isRead;
    }

    public static Message create(String chatRoomId, String senderUserId, String content) {
        return new Message(UUID.randomUUID().toString(), chatRoomId, senderUserId, content, Instant.now(), false);
    }

    public static Message createQuoteMessage(String chatRoomId, String tenantId, String listingSummary) {
        String content = "Hi Landlord, I'm interested in: " + listingSummary;
        return new Message(UUID.randomUUID().toString(), chatRoomId, tenantId, content, Instant.now(), false);
    }

    public static Message rehydrate(String id, String chatRoomId, String senderUserId, String content, Instant sentAt, boolean isRead) {
        return new Message(id, chatRoomId, senderUserId, content, sentAt, isRead);
    }

    public void markAsRead() {
        this.isRead = true;
    }

    public String getId() {
        return id;
    }

    public String getChatRoomId() {
        return chatRoomId;
    }

    public String getSenderUserId() {
        return senderUserId;
    }

    public String getContent() {
        return content;
    }

    public Instant getSentAt() {
        return sentAt;
    }

    public boolean isRead() {
        return isRead;
    }
}
