package com.akyabhishek.authdemo;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

final class SecurityRules {
    private SecurityRules() {
    }

    static void authorize(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/api/auth-demo/public", "/api/auth-demo/login", "/error", "/", "/version", "/java-version",
                        "/config-property", "/config-property-r", "/api1", "/v3/api-docs/**", "/swagger-ui/**")
                .permitAll()
                .requestMatchers("/api/auth-demo/admin").hasRole("ADMIN")
                .requestMatchers("/api/auth-demo/user").hasRole("USER")
                .requestMatchers("/api/auth-demo/**").authenticated()
                .anyRequest().permitAll());
    }

    static void stateless(HttpSecurity http) throws Exception {
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
    }
}
