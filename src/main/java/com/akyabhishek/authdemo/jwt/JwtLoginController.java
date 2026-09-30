package com.akyabhishek.authdemo.jwt;

import java.time.Duration;
import java.util.Map;

import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth-demo")
@Profile("jwt")
public class JwtLoginController {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public JwtLoginController(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(request.username(), request.password()));
        return Map.of("tokenType", "Bearer", "accessToken", jwtService.issue(authentication),
                "expiresIn", Duration.ofMinutes(15).toSeconds());
    }

    public record LoginRequest(String username, String password) {
    }
}
