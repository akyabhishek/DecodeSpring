package com.akyabhishek.authdemo;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("session")
class SessionSecurityIntegrationTests {
    @Autowired
    MockMvc mvc;

    @Test
    void publicEndpointIsPublic() throws Exception {
        mvc.perform(get("/api/auth-demo/public")).andExpect(status().isOk());
    }

    @Test
    void protectedEndpointWithoutAuthenticationIs401() throws Exception {
        mvc.perform(get("/api/auth-demo/profile")).andExpect(status().isUnauthorized());
    }

    @Test
    void userCanReadUserAndDebugEndpoints() throws Exception {
        mvc.perform(get("/api/auth-demo/user").with(user("user").roles("USER")))
                .andExpect(status().isOk());
        mvc.perform(get("/api/auth-demo/debug").with(user("user").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("user"))
                .andExpect(jsonPath("$.authorities[0]").value("ROLE_USER"));
    }

    @Test
    void userCannotReadAdminEndpoint() throws Exception {
        mvc.perform(get("/api/auth-demo/admin").with(user("user").roles("USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminCanReadAdminEndpoint() throws Exception {
        mvc.perform(get("/api/auth-demo/admin").with(user("admin").roles("USER", "ADMIN")))
                .andExpect(status().isOk());
    }
}
