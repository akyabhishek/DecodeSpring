package com.akyabhishek.authdemo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@Profile({"default", "session"})
public class SessionSecurityConfiguration {

    @Bean
    SecurityFilterChain sessionSecurityFilterChain(HttpSecurity http, SecurityResponses responses,
            UserDetailsService users,
            @Value("${auth-demo.remember-me-key}") String rememberMeKey) throws Exception {
        SecurityRules.authorize(http);
        http.formLogin(Customizer.withDefaults())
                .logout(Customizer.withDefaults())
                .rememberMe(remember -> remember.key(rememberMeKey).userDetailsService(users))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(responses)
                        .accessDeniedHandler(responses));
        return http.build();
    }
}
