package com.akyabhishek.authdemo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.akyabhishek.authdemo.apikey.ApiKeyAuthenticationFilter;
import com.akyabhishek.authdemo.apikey.ApiKeyAuthenticationProvider;

@Configuration
@Profile("api-key")
public class ApiKeySecurityConfiguration {
    @Bean
    SecurityFilterChain apiKeySecurityFilterChain(HttpSecurity http, SecurityResponses responses,
            ApiKeyAuthenticationProvider provider) throws Exception {
        SecurityRules.authorize(http);
        SecurityRules.stateless(http);
        http.csrf(csrf -> csrf.ignoringRequestMatchers("/api/auth-demo/**"))
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(responses)
                        .accessDeniedHandler(responses))
                .addFilterBefore(new ApiKeyAuthenticationFilter(new ProviderManager(provider), responses),
                        UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
