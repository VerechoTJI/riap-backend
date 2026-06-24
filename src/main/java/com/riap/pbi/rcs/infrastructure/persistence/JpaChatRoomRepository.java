package com.riap.pbi.rcs.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JpaChatRoomRepository extends JpaRepository<ChatRoomEntity, String> {
    List<ChatRoomEntity> findByTenantIdOrLandlordId(UUID tenantId, UUID landlordId);
}
