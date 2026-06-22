package com.riap.pbi.rcs.web;

import com.riap.pbi.rcs.domain.Message;
import com.riap.pbi.rcs.port.AuthenticationProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.net.URI;
import java.time.Instant;

import static org.mockito.Mockito.*;

class ChatWebSocketHandlerTest {

    private AuthenticationProvider authenticationProvider;
    private ChatWebSocketHandler handler;
    private WebSocketSession session;

    @BeforeEach
    void setUp() {
        authenticationProvider = mock(AuthenticationProvider.class);
        handler = new ChatWebSocketHandler(authenticationProvider);
        session = mock(WebSocketSession.class);
    }

    @Test
    void testAfterConnectionEstablished_ValidToken() throws Exception {
        URI uri = new URI("ws://localhost/ws/chat/connect?token=valid-token");
        when(session.getUri()).thenReturn(uri);
        when(authenticationProvider.validateTokenAndGetUserId("valid-token")).thenReturn("user-1");

        handler.afterConnectionEstablished(session);

        verify(session).sendMessage(new TextMessage("{\"connectionStatus\":\"CONNECTED\"}"));
    }

    @Test
    void testBroadcastToRoom() throws Exception {
        URI uri = new URI("ws://localhost/ws/chat/connect?token=valid-token");
        when(session.getUri()).thenReturn(uri);
        when(session.isOpen()).thenReturn(true);
        when(authenticationProvider.validateTokenAndGetUserId("valid-token")).thenReturn("user-1");

        handler.afterConnectionEstablished(session);

        Message msg = Message.rehydrate("msg-1", "room-1", "user-2", "Hello Broadcaster", Instant.now(), false);
        handler.broadcastToRoom("room-1", msg);

        verify(session).sendMessage(new TextMessage("{\"chatRoomId\":\"room-1\", \"content\":\"Hello Broadcaster\"}"));
    }
}
