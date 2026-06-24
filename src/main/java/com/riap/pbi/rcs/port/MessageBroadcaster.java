package com.riap.pbi.rcs.port;

public interface MessageBroadcaster {
    void broadcastToRoom(String chatRoomId, com.riap.pbi.rcs.domain.Message message);
    void notifyReadReceipt(String chatRoomId, String readerId);
}
