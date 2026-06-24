package com.riap.pbi.rcs.web;

import com.riap.pbi.rcs.domain.ChatRoom;
import com.riap.pbi.rcs.domain.Message;
import com.riap.pbi.rcs.port.AuthenticationProvider;
import com.riap.pbi.rcs.port.LmsClient;
import com.riap.pbi.rcs.service.ChatService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import java.util.UUID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ChatRestController.class, excludeAutoConfiguration = {
    org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration.class,
    org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration.class,
    org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration.class
})
class ChatRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ChatService chatService;

    @MockBean
    private AuthenticationProvider authenticationProvider;

    @MockBean
    private LmsClient lmsClient;

    @BeforeEach
    void setUp() {
        when(authenticationProvider.validateTokenAndGetUserId(any())).thenReturn("00000000-0000-0000-0000-000000000001");
    }

    @Test
    void testCreateChatRoom() throws Exception {
        UUID tenantId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        UUID landlordId = UUID.fromString("00000000-0000-0000-0000-000000000002");
        ChatRoom room = ChatRoom.rehydrate("room-1", tenantId, landlordId, "list-1");
        when(lmsClient.getLandlordIdForListing("list-1")).thenReturn(landlordId);
        when(chatService.createOrGetRoom(tenantId, landlordId, "list-1")).thenReturn(room);

        String requestJson = "{\"listingId\":\"list-1\"}";

        mockMvc.perform(post("/api/chat/createChatRoom")
                .header("Authorization", "Bearer valid-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.chatroomId").value("room-1"))
                .andExpect(jsonPath("$.landlordId").value("00000000-0000-0000-0000-000000000002"));
    }

    @Test
    void testSendMessage() throws Exception {
        UUID tenantId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        Message msg = Message.rehydrate("msg-1", "room-1", tenantId, "Hello", Instant.now(), false);
        when(chatService.saveAndBroadcastMessage("room-1", tenantId, "Hello")).thenReturn(msg);

        String requestJson = "{\"chatRoomId\":\"room-1\",\"content\":\"Hello\"}";

        mockMvc.perform(post("/api/chat/sendMessage")
                .header("Authorization", "Bearer valid-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.messageId").value("msg-1"));
    }

    @Test
    void testGetRooms() throws Exception {
        UUID tenantId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        UUID landlordId = UUID.fromString("00000000-0000-0000-0000-000000000002");
        com.riap.pbi.rcs.domain.ChatRoomDTO dto = new com.riap.pbi.rcs.domain.ChatRoomDTO(
                "room-1", tenantId, landlordId, "list-1", "Alice", "Beautiful Apartment", true
        );
        when(chatService.getUserChatRooms(tenantId)).thenReturn(java.util.Collections.singletonList(dto));

        mockMvc.perform(get("/api/chat/rooms")
                .header("Authorization", "Bearer valid-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("room-1"))
                .andExpect(jsonPath("$[0].otherUserName").value("Alice"))
                .andExpect(jsonPath("$[0].listingTitle").value("Beautiful Apartment"));
    }

    @Test
    void testHasUnread() throws Exception {
        UUID tenantId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        when(chatService.hasGlobalUnread(tenantId)).thenReturn(true);
        
        mockMvc.perform(get("/api/chat/hasUnread")
                .header("Authorization", "Bearer valid-token"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }
}
