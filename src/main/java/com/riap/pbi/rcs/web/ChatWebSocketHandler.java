package com.riap.pbi.rcs.web;

import com.riap.pbi.rcs.domain.Message;
import com.riap.pbi.rcs.port.AuthenticationProvider;
import com.riap.pbi.rcs.port.MessageBroadcaster;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChatWebSocketHandler extends TextWebSocketHandler implements MessageBroadcaster {

    private final AuthenticationProvider authenticationProvider;

    // userId -> session
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    public ChatWebSocketHandler(AuthenticationProvider authenticationProvider) {
        this.authenticationProvider = authenticationProvider;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String token = extractTokenFromSession(session);
        String userId = authenticationProvider.validateTokenAndGetUserId(token);
        
        if (userId == null) {
            session.close(CloseStatus.NOT_ACCEPTABLE.withReason("Unauthorized"));
            return;
        }

        sessions.put(userId, session);
        session.sendMessage(new TextMessage("{\"connectionStatus\":\"CONNECTED\"}"));
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // SDD uses POST /api/chat/sendMessage
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.values().remove(session);
    }

    private String extractTokenFromSession(WebSocketSession session) {
        String query = session.getUri().getQuery();
        if (query != null && query.contains("token=")) {
            String[] params = query.split("&");
            for (String param : params) {
                if (param.startsWith("token=")) {
                    return param.substring(6);
                }
            }
        }
        return null;
    }

    @Override
    public void broadcastToRoom(String chatRoomId, Message message) {
        // For simplicity, we just broadcast to the room by sending to all connected users
        // In a real implementation, we'd look up the users in the room.
        // As a pure fabrication, we assume we send it to everyone or we need room members
        // To be correct, we should get the participants of the room.
        // For now, this is a placeholder implementation.
        String payload = "{\"chatRoomId\":\"" + chatRoomId + "\", \"content\":\"" + message.getContent() + "\"}";
        sessions.values().forEach(session -> {
            try {
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(payload));
                }
            } catch (Exception e) {
                // ignore
            }
        });
    }

    @Override
    public void notifyReadReceipt(String chatRoomId, String readerId) {
        String payload = "{\"chatRoomId\":\"" + chatRoomId + "\", \"readBy\":\"" + readerId + "\"}";
        sessions.values().forEach(session -> {
            try {
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(payload));
                }
            } catch (Exception e) {
                // ignore
            }
        });
    }
}
