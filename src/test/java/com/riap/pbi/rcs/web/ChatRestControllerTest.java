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
        when(authenticationProvider.validateTokenAndGetUserId(any())).thenReturn("tenant-1");
    }

    @Test
    void testCreateChatRoom() throws Exception {
        ChatRoom room = ChatRoom.rehydrate("room-1", "tenant-1", "landlord-1", "list-1");
        when(lmsClient.getLandlordIdForListing("list-1")).thenReturn("landlord-1");
        when(chatService.createOrGetRoom("tenant-1", "landlord-1", "list-1")).thenReturn(room);

        String requestJson = "{\"listingId\":\"list-1\"}";

        mockMvc.perform(post("/api/chat/createChatRoom")
                .header("Authorization", "Bearer valid-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.chatroomId").value("room-1"))
                .andExpect(jsonPath("$.landlordId").value("landlord-1"));
    }

    @Test
    void testSendMessage() throws Exception {
        Message msg = Message.rehydrate("msg-1", "room-1", "tenant-1", "Hello", Instant.now(), false);
        when(chatService.saveAndBroadcastMessage("room-1", "tenant-1", "Hello")).thenReturn(msg);

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
        com.riap.pbi.rcs.domain.ChatRoomDTO dto = new com.riap.pbi.rcs.domain.ChatRoomDTO(
                "room-1", "tenant-1", "landlord-1", "list-1", "Alice", "Beautiful Apartment", true
        );
        when(chatService.getUserChatRooms("tenant-1")).thenReturn(java.util.Collections.singletonList(dto));

        mockMvc.perform(get("/api/chat/rooms")
                .header("Authorization", "Bearer valid-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("room-1"))
                .andExpect(jsonPath("$[0].otherUserName").value("Alice"))
                .andExpect(jsonPath("$[0].listingTitle").value("Beautiful Apartment"));
    }

    @Test
    void testHasUnread() throws Exception {
        when(chatService.hasGlobalUnread("tenant-1")).thenReturn(true);
        
        mockMvc.perform(get("/api/chat/hasUnread")
                .header("Authorization", "Bearer valid-token"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }
}
