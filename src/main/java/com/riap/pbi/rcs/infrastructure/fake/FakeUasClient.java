package com.riap.pbi.rcs.infrastructure.fake;

import com.riap.pbi.rcs.port.UasClient;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class FakeUasClient implements UasClient {
    
    private final Map<String, String> users = new HashMap<>();
    
    public FakeUasClient() {
        users.put("1", "Alice Chen");
        users.put("2", "Bob Wang");
        users.put("3", "系統管理員");
        
        // Keep old ones just in case
        users.put("tenant-1", "Alice Chen");
        users.put("landlord-1", "Bob Wang");
    }
    
    @Override
    public String getUserProfile(String userId) {
        return users.getOrDefault(userId, "使用者 " + userId);
    }
}
