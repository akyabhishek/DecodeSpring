package com.akyabhishek.authdemo;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth-demo")
public class AuthDemoController {

    @GetMapping("/public")
    public Map<String, Object> publicEndpoint() {
        return Map.of("message", "Public authentication-lab endpoint", "authenticated", false);
    }

    @GetMapping("/profile")
    public Map<String, Object> profile(Authentication authentication) {
        return Map.of("message", "Authenticated profile", "name", authentication.getName());
    }

    @GetMapping("/user")
    public Map<String, Object> user(Authentication authentication) {
        return Map.of("message", "USER endpoint", "name", authentication.getName());
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, Object> admin(Authentication authentication) {
        return Map.of("message", "ADMIN endpoint", "name", authentication.getName());
    }

    @GetMapping("/debug")
    public Map<String, Object> debug() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("authenticated", authentication != null && authentication.isAuthenticated());
        result.put("name", authentication == null ? null : authentication.getName());
        result.put("authenticationClass", authentication == null ? null : authentication.getClass().getSimpleName());
        Object principal = authentication == null ? null : authentication.getPrincipal();
        result.put("principalClass", principal == null ? null : principal.getClass().getSimpleName());
        List<String> authorities = authentication == null ? List.of()
                : authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).sorted().toList();
        result.put("authorities", authorities);
        return result;
    }
}
