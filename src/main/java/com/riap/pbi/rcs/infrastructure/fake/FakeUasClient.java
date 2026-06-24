package com.riap.pbi.rcs.infrastructure.fake;

import com.riap.pbi.rcs.port.UasClient;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class FakeUasClient implements UasClient {
    
    private final Map<String, String> users = new HashMap<>();
    
    public FakeUasClient() {
        users.put("00000000-0000-0000-0000-000000000001", "Alice Chen");
        users.put("00000000-0000-0000-0000-000000000002", "Bob Wang");
        users.put("00000000-0000-0000-0000-000000000003", "系統管理員");
    }
    
    @Override
    public String getUserProfile(String userId) {
        return users.getOrDefault(userId, "使用者 " + userId);
    }
}
