package com.akyabhishek.authdemo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.akyabhishek.authdemo.jwt.JwtAuthenticationFilter;
import com.akyabhishek.authdemo.jwt.JwtAuthenticationProvider;

@Configuration
@Profile("jwt")
public class JwtSecurityConfiguration {
    @Bean
    SecurityFilterChain jwtSecurityFilterChain(HttpSecurity http, SecurityResponses responses,
            JwtAuthenticationProvider provider) throws Exception {
        SecurityRules.authorize(http);
        http.csrf(csrf -> csrf.ignoringRequestMatchers("/api/auth-demo/**"))
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(responses)
                        .accessDeniedHandler(responses))
                .addFilterBefore(new JwtAuthenticationFilter(new ProviderManager(provider)),
                        UsernamePasswordAuthenticationFilter.class);
        SecurityRules.stateless(http);
        return http.build();
    }
}
