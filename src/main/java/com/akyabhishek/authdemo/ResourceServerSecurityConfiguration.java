package com.akyabhishek.authdemo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@Profile("resource-server")
public class ResourceServerSecurityConfiguration {
    @Bean
    SecurityFilterChain resourceServerSecurityFilterChain(HttpSecurity http, SecurityResponses responses)
            throws Exception {
        SecurityRules.authorize(http);
        SecurityRules.stateless(http);
        http.oauth2ResourceServer(resource -> resource.jwt(Customizer.withDefaults()))
                .csrf(csrf -> csrf.ignoringRequestMatchers("/api/auth-demo/**"))
                .exceptionHandling(exceptions -> exceptions.accessDeniedHandler(responses));
        return http.build();
    }
}
