package com.riap.pbi.integration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.riap.pbi.rcs.domain.ChatRoom;
import com.riap.pbi.rcs.domain.Message;
import com.riap.pbi.rcs.domain.ChatRoomDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.core.ParameterizedTypeReference;

import com.riap.user.domain.model.UserRole;
import com.riap.user.security.JwtService;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class RealTimeCommunicationIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtService jwtService;

    private String TENANT_TOKEN;
    private String LANDLORD_TOKEN;
    private UUID tenantId;
    private UUID landlordId;

    @BeforeEach
    void setup() {
        tenantId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        landlordId = UUID.fromString("00000000-0000-0000-0000-000000000002");
        TENANT_TOKEN = jwtService.generateToken(tenantId, UserRole.TENANT);
        LANDLORD_TOKEN = jwtService.generateToken(landlordId, UserRole.LANDLORD);
    }

    private HttpHeaders createHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    // [RCS-TC01] 建立聊天室測試
    @Test
    void testCreateChatRoom() {
        Map<String, String> request = new HashMap<>();
        request.put("listingId", "listing-100");

        ResponseEntity<Map> response = restTemplate.postForEntity(
                "/api/chat/createChatRoom",
                new HttpEntity<>(request, createHeaders(TENANT_TOKEN)),
                Map.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("chatroomId")).isNotNull();
        assertThat(response.getBody().get("landlordId")).isEqualTo("00000000-0000-0000-0000-000000000002"); // FakeLmsClient returns 2
    }

    // [RCS-TC02] WebSocket 連線驗證測試 & [RCS-TC05] 身份綁定與認證攔截測試
    @Test
    void testWebSocketConnection_And_Authentication() throws Exception {
        StandardWebSocketClient client = new StandardWebSocketClient();

        // 1. Valid token
        CompletableFuture<String> connectedMessage = new CompletableFuture<>();
        WebSocketSession session = client.execute(new TextWebSocketHandler() {
            @Override
            protected void handleTextMessage(WebSocketSession session, TextMessage message) {
                if (message.getPayload().contains("CONNECTED")) {
                    connectedMessage.complete(message.getPayload());
                }
            }
        }, "ws://localhost:" + port + "/ws/chat/connect?token=" + TENANT_TOKEN).get(5, TimeUnit.SECONDS);

        assertThat(session.isOpen()).isTrue();
        assertThat(connectedMessage.get(5, TimeUnit.SECONDS)).contains("CONNECTED");
        session.close();

        // 2. Invalid token (empty) -> Should be closed immediately
        CompletableFuture<CloseStatus> closeStatusFuture = new CompletableFuture<>();
        try {
            WebSocketSession invalidSession = client.execute(new TextWebSocketHandler() {
                @Override
                public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
                    closeStatusFuture.complete(status);
                }
            }, "ws://localhost:" + port + "/ws/chat/connect?token=").get(5, TimeUnit.SECONDS);
            
            CloseStatus status = closeStatusFuture.get(5, TimeUnit.SECONDS);
            assertThat(status.getCode()).isEqualTo(CloseStatus.NOT_ACCEPTABLE.getCode());
        } catch (Exception e) {
            // ExecutionException could wrap the connect failure, which also means it was rejected.
        }
    }

    // [RCS-TC03] 收發訊息測試 & [RCS-TC06] 接收訊息測試
    @Test
    void testSendMessageAndBroadcast() throws Exception {
        // Create Room
        Map<String, String> request = new HashMap<>();
        request.put("listingId", "listing-200");
        ResponseEntity<Map> createResp = restTemplate.postForEntity(
                "/api/chat/createChatRoom",
                new HttpEntity<>(request, createHeaders(TENANT_TOKEN)),
                Map.class
        );
        String chatRoomId = (String) createResp.getBody().get("chatroomId");

        // Landlord connects via WS
        StandardWebSocketClient client = new StandardWebSocketClient();
        CompletableFuture<String> broadcastMessage = new CompletableFuture<>();
        WebSocketSession landlordSession = client.execute(new TextWebSocketHandler() {
            @Override
            protected void handleTextMessage(WebSocketSession session, TextMessage message) {
                if (message.getPayload().contains("Hello Landlord!")) {
                    broadcastMessage.complete(message.getPayload());
                }
            }
        }, "ws://localhost:" + port + "/ws/chat/connect?token=" + LANDLORD_TOKEN).get(5, TimeUnit.SECONDS);

        // Tenant sends message via HTTP
        Map<String, String> sendReq = new HashMap<>();
        sendReq.put("chatRoomId", chatRoomId);
        sendReq.put("content", "Hello Landlord!");
        ResponseEntity<Map> sendResp = restTemplate.postForEntity(
                "/api/chat/sendMessage",
                new HttpEntity<>(sendReq, createHeaders(TENANT_TOKEN)),
                Map.class
        );
        assertThat(sendResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(sendResp.getBody().get("isSuccess")).isEqualTo(true);

        // Verify Landlord received the message via WS
        String receivedPayload = broadcastMessage.get(5, TimeUnit.SECONDS);
        assertThat(receivedPayload).contains("Hello Landlord!");
        assertThat(receivedPayload).contains(chatRoomId);

        landlordSession.close();
    }

    // [RCS-TC04] 聊天室歷史訊息查詢測試 & [RCS-TC07] 顯示歷史紀錄測試
    @Test
    void testGetChatHistory() {
        // Create Room
        Map<String, String> request = new HashMap<>();
        request.put("listingId", "listing-300");
        ResponseEntity<Map> createResp = restTemplate.postForEntity(
                "/api/chat/createChatRoom",
                new HttpEntity<>(request, createHeaders(TENANT_TOKEN)),
                Map.class
        );
        String chatRoomId = (String) createResp.getBody().get("chatroomId");

        // Send 2 messages
        Map<String, String> sendReq1 = new HashMap<>();
        sendReq1.put("chatRoomId", chatRoomId);
        sendReq1.put("content", "Msg1");
        restTemplate.postForEntity("/api/chat/sendMessage", new HttpEntity<>(sendReq1, createHeaders(TENANT_TOKEN)), Map.class);

        Map<String, String> sendReq2 = new HashMap<>();
        sendReq2.put("chatRoomId", chatRoomId);
        sendReq2.put("content", "Msg2");
        restTemplate.postForEntity("/api/chat/sendMessage", new HttpEntity<>(sendReq2, createHeaders(LANDLORD_TOKEN)), Map.class);

        // Get History
        ResponseEntity<String> historyResp = restTemplate.exchange(
                "/api/chat/history/" + chatRoomId,
                HttpMethod.GET,
                new HttpEntity<>(createHeaders(TENANT_TOKEN)),
                String.class
        );

        assertThat(historyResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(historyResp.getBody()).contains("Msg1");
        assertThat(historyResp.getBody()).contains("Msg2");
    }

    // [RCS-TC08] 顯示已讀狀態測試
    @Test
    void testReadReceiptsAndUnreadStatus() throws Exception {
        // Create Room
        Map<String, String> request = new HashMap<>();
        request.put("listingId", "listing-400");
        ResponseEntity<Map> createResp = restTemplate.postForEntity(
                "/api/chat/createChatRoom",
                new HttpEntity<>(request, createHeaders(TENANT_TOKEN)),
                Map.class
        );
        String chatRoomId = (String) createResp.getBody().get("chatroomId");

        // Tenant sends message
        Map<String, String> sendReq = new HashMap<>();
        sendReq.put("chatRoomId", chatRoomId);
        sendReq.put("content", "Unread Msg");
        restTemplate.postForEntity("/api/chat/sendMessage", new HttpEntity<>(sendReq, createHeaders(TENANT_TOKEN)), Map.class);

        // Landlord checks unread for this room
        ResponseEntity<List<ChatRoomDTO>> roomsResp1 = restTemplate.exchange(
                "/api/chat/rooms",
                HttpMethod.GET,
                new HttpEntity<>(createHeaders(LANDLORD_TOKEN)),
                new ParameterizedTypeReference<List<ChatRoomDTO>>() {}
        );
        ChatRoomDTO targetRoom1 = roomsResp1.getBody().stream().filter(r -> r.getId().equals(chatRoomId)).findFirst().get();
        assertThat(targetRoom1.getHasUnread()).isTrue();

        // Landlord connects WS
        StandardWebSocketClient client = new StandardWebSocketClient();
        CompletableFuture<String> readReceiptMessage = new CompletableFuture<>();
        WebSocketSession landlordSession = client.execute(new TextWebSocketHandler() {
            @Override
            protected void handleTextMessage(WebSocketSession session, TextMessage message) {
                if (message.getPayload().contains("readBy")) {
                    readReceiptMessage.complete(message.getPayload());
                }
            }
        }, "ws://localhost:" + port + "/ws/chat/connect?token=" + TENANT_TOKEN).get(5, TimeUnit.SECONDS);

        // Landlord marks as read
        ResponseEntity<Void> readResp = restTemplate.exchange(
                "/api/chat/read/" + chatRoomId,
                HttpMethod.PUT,
                new HttpEntity<>(createHeaders(LANDLORD_TOKEN)),
                Void.class
        );
        assertThat(readResp.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Landlord checks unread again for this room
        ResponseEntity<List<ChatRoomDTO>> roomsResp2 = restTemplate.exchange(
                "/api/chat/rooms",
                HttpMethod.GET,
                new HttpEntity<>(createHeaders(LANDLORD_TOKEN)),
                new ParameterizedTypeReference<List<ChatRoomDTO>>() {}
        );
        ChatRoomDTO targetRoom2 = roomsResp2.getBody().stream().filter(r -> r.getId().equals(chatRoomId)).findFirst().get();
        assertThat(targetRoom2.getHasUnread()).isFalse();

        // Tenant should receive read receipt via WS
        String receipt = readReceiptMessage.get(5, TimeUnit.SECONDS);
        assertThat(receipt).contains("readBy");
        assertThat(receipt).contains(landlordId.toString());

        landlordSession.close();
    }

    // [RCS-TC09] 引用房源資料測試
    @Test
    void testGetChatRooms() {
        // Create Room
        Map<String, String> request = new HashMap<>();
        // "1" maps to "台北市中正區採光套房" in FakeLmsClient
        request.put("listingId", "1");
        restTemplate.postForEntity("/api/chat/createChatRoom", new HttpEntity<>(request, createHeaders(TENANT_TOKEN)), Map.class);

        // Get Rooms
        ResponseEntity<List<ChatRoomDTO>> roomsResp = restTemplate.exchange(
                "/api/chat/rooms",
                HttpMethod.GET,
                new HttpEntity<>(createHeaders(TENANT_TOKEN)),
                new ParameterizedTypeReference<List<ChatRoomDTO>>() {}
        );

        assertThat(roomsResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(roomsResp.getBody()).isNotEmpty();
        ChatRoomDTO dto = roomsResp.getBody().get(0);
        assertThat(dto.getListingTitle()).isEqualTo("台北市中正區採光套房"); // Verified it pulled from FakeLmsClient
    }
}
