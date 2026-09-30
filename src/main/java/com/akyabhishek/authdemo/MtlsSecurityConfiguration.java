package com.akyabhishek.authdemo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@Profile("mtls")
public class MtlsSecurityConfiguration {
    @Bean
    SecurityFilterChain mtlsSecurityFilterChain(HttpSecurity http, SecurityResponses responses,
            UserDetailsService users) throws Exception {
        SecurityRules.authorize(http);
        http.x509(x509 -> x509.subjectPrincipalRegex("CN=(.*?)(?:,|$)").userDetailsService(users))
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(responses)
                        .accessDeniedHandler(responses));
        return http.build();
    }
}
