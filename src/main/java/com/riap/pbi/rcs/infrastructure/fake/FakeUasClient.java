package com.riap.pbi.rcs.infrastructure.fake;

import com.riap.pbi.rcs.port.UasClient;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class FakeUasClient implements UasClient {
    
    private final Map<String, String> users = new HashMap<>();
    
    public FakeUasClient() {
        users.put("tenant-1", "Alice Chen");
        users.put("tenant-2", "Bob Wang");
        users.put("tenant-3", "Charlie Lin");
        users.put("landlord-1", "Mr. Landlord (David)");
        users.put("landlord-2", "Mrs. Landlord (Eva)");
    }
    
    @Override
    public String getUserProfile(String userId) {
        return users.getOrDefault(userId, "使用者 " + userId);
    }
}
