package com.riap.user.infrastructure.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.riap.user.application.service.AuthenticationService;
import com.riap.user.domain.model.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * End-to-end UAS security flow: login issues a JWT (UAS-F-01/F-03), {@link
 * com.riap.user.security.RequireRole} enforces role-based access (UAS-F-02/F-04),
 * and logout revokes the token (UAS-F-01).
 */
@SpringBootTest
@AutoConfigureMockMvc
class AuthSecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private ObjectMapper objectMapper;

    private String login(String account, String password) throws Exception {
        String body = objectMapper.writeValueAsString(Map.of("account", account, "password", password));
        String json = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(json).get("token").asText();
    }

    private static String bearer(String token) {
        return "Bearer " + token;
    }

    @Test
    void loginIssuesToken_rbacEnforced_andLogoutRevokes() throws Exception {
        authenticationService.register("admin-sec@example.com", "pw", UserRole.ADMIN);
        authenticationService.register("tenant-sec@example.com", "pw", UserRole.TENANT);
        String adminToken = login("admin-sec@example.com", "pw");
        String tenantToken = login("tenant-sec@example.com", "pw");

        // Protected endpoint without a token -> 401.
        mockMvc.perform(get("/api/auth/me")).andExpect(status().isUnauthorized());

        // Any authenticated user can read /me and sees their own role.
        mockMvc.perform(get("/api/auth/me").header("Authorization", bearer(tenantToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("TENANT"));

        // Role enforcement: tenant is forbidden from the admin-only endpoint, admin is allowed.
        mockMvc.perform(get("/api/auth/admin-area").header("Authorization", bearer(tenantToken)))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/api/auth/admin-area").header("Authorization", bearer(adminToken)))
                .andExpect(status().isOk());

        // Logout revokes the token; the same token is then rejected.
        mockMvc.perform(post("/api/auth/logout").header("Authorization", bearer(adminToken)))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/api/auth/me").header("Authorization", bearer(adminToken)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void invalidLoginReturns401AndNoToken() throws Exception {
        authenticationService.register("real@example.com", "rightpw", UserRole.TENANT);
        String body = objectMapper.writeValueAsString(Map.of("account", "real@example.com", "password", "wrongpw"));

        String json = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isUnauthorized())
                .andReturn().getResponse().getContentAsString();

        assertFalse(objectMapper.readTree(json).get("success").asBoolean());
    }
}
