package com.akyabhishek.authdemo;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Duration;
import java.time.Instant;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.akyabhishek.authdemo.jwt.JwtService;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("jwt")
class JwtSecurityIntegrationTests {
    private static final String SECRET = "change-this-local-demo-secret-at-least-32-bytes";
    private static final Pattern TOKEN = Pattern.compile("\\\"accessToken\\\":\\\"([^\\\"]+)\\\"");

    @Autowired
    MockMvc mvc;

    @Test
    void missingAndInvalidJwtAre401() throws Exception {
        mvc.perform(get("/api/auth-demo/profile")).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/auth-demo/profile").header(HttpHeaders.AUTHORIZATION, "Bearer invalid.jwt.value"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void loginIssuesJwtThatAuthenticatesARequest() throws Exception {
        String json = mvc.perform(post("/api/auth-demo/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"user\",\"password\":\"password\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        Matcher matcher = TOKEN.matcher(json);
        if (!matcher.find()) {
            throw new AssertionError("Login response did not contain an access token");
        }
        mvc.perform(get("/api/auth-demo/profile")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + matcher.group(1)))
                .andExpect(status().isOk());
    }

    @Test
    void expiredJwtIs401() throws Exception {
        JwtService expiredTokenService = new JwtService(SECRET, Duration.ofMinutes(1));
        String token = expiredTokenService.issue(UsernamePasswordAuthenticationToken.authenticated(
                "user", "", java.util.List.of(new SimpleGrantedAuthority("ROLE_USER"))),
                Instant.now().minus(Duration.ofMinutes(2)));
        mvc.perform(get("/api/auth-demo/profile")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isUnauthorized());
    }
}
