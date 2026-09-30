package com.akyabhishek.authdemo;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
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
@ActiveProfiles("basic")
class BasicSecurityIntegrationTests {
    @Autowired
    MockMvc mvc;

    @Test
    void validAndInvalidCredentialsPassThroughBasicFilter() throws Exception {
        mvc.perform(get("/api/auth-demo/profile").with(httpBasic("user", "password")))
                .andExpect(status().isOk());
        mvc.perform(get("/api/auth-demo/profile").with(httpBasic("user", "wrong")))
                .andExpect(status().isUnauthorized());
    }
}
