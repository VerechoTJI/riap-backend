package com.riap.pbi.rcs.infrastructure.uas;

import com.riap.pbi.rcs.port.UasClient;
import com.riap.user.domain.model.UserAccountEntity;
import com.riap.user.domain.repository.UserAccountRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UasIntegrationAdapter implements UasClient {

    private final UserAccountRepository userAccountRepository;

    public UasIntegrationAdapter(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }

    @Override
    public String getUserProfile(String userId) {
        try {
            UUID id = UUID.fromString(userId);
            return userAccountRepository.findById(id)
                    .map(UserAccountEntity::getLoginIdentifier)
                    // If login identifier is bob, capitalize it nicely
                    .map(name -> name.substring(0, 1).toUpperCase() + name.substring(1))
                    .orElse("使用者 " + userId);
        } catch (IllegalArgumentException e) {
            return "使用者 " + userId;
        }
    }
}
