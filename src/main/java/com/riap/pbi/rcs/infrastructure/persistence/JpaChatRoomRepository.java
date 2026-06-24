package com.riap.pbi.rcs.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JpaChatRoomRepository extends JpaRepository<ChatRoomEntity, String> {
    List<ChatRoomEntity> findByTenantIdOrLandlordId(String tenantId, String landlordId);
}
