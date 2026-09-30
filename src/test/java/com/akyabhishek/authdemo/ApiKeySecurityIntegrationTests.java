package com.akyabhishek.authdemo;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("api-key")
class ApiKeySecurityIntegrationTests {
    @Autowired
    MockMvc mvc;

    @Test
    void customProviderAcceptsOnlyTheConfiguredKey() throws Exception {
        mvc.perform(get("/api/auth-demo/profile")).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/auth-demo/profile").header("X-API-KEY", "wrong"))
                .andExpect(status().isUnauthorized());
        mvc.perform(get("/api/auth-demo/profile").header("X-API-KEY", "demo-key"))
                .andExpect(status().isOk());
    }

    @Test
    void apiKeyUserCannotAccessAdminEndpoint() throws Exception {
        mvc.perform(get("/api/auth-demo/admin").header("X-API-KEY", "demo-key"))
                .andExpect(status().isForbidden());
    }
}
