package com.riap.pbi.rcs.web;

import com.riap.pbi.rcs.domain.ChatRoom;
import com.riap.pbi.rcs.domain.Message;
import com.riap.pbi.rcs.port.AuthenticationProvider;
import com.riap.pbi.rcs.port.LmsClient;
import com.riap.pbi.rcs.service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*")
public class ChatRestController {

    private final ChatService chatService;
    private final AuthenticationProvider authenticationProvider;
    private final LmsClient lmsClient;

    public ChatRestController(ChatService chatService, AuthenticationProvider authenticationProvider, LmsClient lmsClient) {
        this.chatService = chatService;
        this.authenticationProvider = authenticationProvider;
        this.lmsClient = lmsClient;
    }

    private String extractToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return authHeader;
    }

    private UUID getUserId(String authHeader) {
        String token = extractToken(authHeader);
        String userId = authenticationProvider.validateTokenAndGetUserId(token);
        if (userId == null) {
            throw new RuntimeException("Unauthorized");
        }
        return UUID.fromString(userId);
    }

    @PostMapping("/createChatRoom")
    public ResponseEntity<Map<String, Object>> createChatRoom(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, String> request) {
        UUID tenantId = getUserId(authHeader);
        String listingId = request.get("listingId");
        
        UUID landlordId = lmsClient.getLandlordIdForListing(listingId);

        ChatRoom chatRoom = chatService.createOrGetRoom(tenantId, landlordId, listingId);

        Map<String, Object> response = new HashMap<>();
        response.put("chatroomId", chatRoom.getId());
        response.put("landlordId", chatRoom.getLandlordId().toString());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/sendMessage")
    public ResponseEntity<Map<String, Object>> sendMessage(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, String> request) {
        UUID senderId = getUserId(authHeader);
        String chatRoomId = request.get("chatRoomId");
        String content = request.get("content");

        Message msg = chatService.saveAndBroadcastMessage(chatRoomId, senderId, content);

        Map<String, Object> response = new HashMap<>();
        response.put("isSuccess", true);
        response.put("messageId", msg.getId());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/rooms")
    public ResponseEntity<List<com.riap.pbi.rcs.domain.ChatRoomDTO>> getRooms(@RequestHeader("Authorization") String authHeader) {
        UUID userId = getUserId(authHeader);
        return ResponseEntity.ok(chatService.getUserChatRooms(userId));
    }

    @GetMapping("/history/{roomId}")
    public ResponseEntity<List<Message>> getHistory(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable("roomId") String roomId) {
        UUID userId = getUserId(authHeader);
        return ResponseEntity.ok(chatService.getMessages(roomId, userId));
    }

    @PutMapping("/read/{roomId}")
    public ResponseEntity<Void> markAsRead(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable("roomId") String roomId) {
        UUID userId = getUserId(authHeader);
        chatService.markMessagesAsRead(roomId, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/hasUnread")
    public ResponseEntity<Boolean> hasUnread(@RequestHeader("Authorization") String authHeader) {
        UUID userId = getUserId(authHeader);
        return ResponseEntity.ok(chatService.hasGlobalUnread(userId));
    }

    @DeleteMapping("/rooms")
    public ResponseEntity<Map<String, Object>> deleteAllRooms(@RequestHeader("Authorization") String authHeader) {
        UUID userId = getUserId(authHeader);
        chatService.deleteAllRooms(userId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("isSuccess", true);
        return ResponseEntity.ok(response);
    }
}
