package com.riap.pbi.rcs.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JpaMessageRepository extends JpaRepository<ChatMessageEntity, String> {
    List<ChatMessageEntity> findByChatRoomIdOrderBySentAtAsc(String chatRoomId);
}
