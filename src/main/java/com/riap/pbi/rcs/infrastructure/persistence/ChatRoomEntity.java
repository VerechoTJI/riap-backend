package com.riap.pbi.rcs.infrastructure.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "chat_rooms")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomEntity {

    @Id
    private String id;
    private UUID tenantId;
    private UUID landlordId;
    private String listingId;
}
