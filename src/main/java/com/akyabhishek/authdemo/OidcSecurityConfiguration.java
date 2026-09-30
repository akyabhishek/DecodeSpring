package com.akyabhishek.authdemo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@Profile("oidc")
public class OidcSecurityConfiguration {
    @Bean
    SecurityFilterChain oidcSecurityFilterChain(HttpSecurity http, SecurityResponses responses) throws Exception {
        SecurityRules.authorize(http);
        http.oauth2Login(Customizer.withDefaults())
                .logout(Customizer.withDefaults())
                .exceptionHandling(exceptions -> exceptions.accessDeniedHandler(responses));
        return http.build();
    }
}
