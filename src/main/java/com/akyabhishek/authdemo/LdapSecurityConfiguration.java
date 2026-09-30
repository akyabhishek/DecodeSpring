package com.akyabhishek.authdemo;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.ldap.DefaultSpringSecurityContextSource;
import org.springframework.security.ldap.authentication.BindAuthenticator;
import org.springframework.security.ldap.authentication.LdapAuthenticationProvider;
import org.springframework.security.ldap.userdetails.DefaultLdapAuthoritiesPopulator;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@Profile("ldap")
public class LdapSecurityConfiguration {
    @Bean
    DefaultSpringSecurityContextSource ldapContextSource(
            @Value("${auth-demo.ldap.url}") String url,
            @Value("${auth-demo.ldap.base}") String base,
            @Value("${auth-demo.ldap.manager-dn}") String managerDn,
            @Value("${auth-demo.ldap.manager-password}") String managerPassword) {
        DefaultSpringSecurityContextSource contextSource =
                new DefaultSpringSecurityContextSource(List.of(url), base);
        contextSource.setUserDn(managerDn);
        contextSource.setPassword(managerPassword);
        return contextSource;
    }

    @Bean
    LdapAuthenticationProvider ldapAuthenticationProvider(DefaultSpringSecurityContextSource contextSource,
            @Value("${auth-demo.ldap.user-dn-pattern}") String userDnPattern,
            @Value("${auth-demo.ldap.group-search-base}") String groupSearchBase) {
        BindAuthenticator authenticator = new BindAuthenticator(contextSource);
        authenticator.setUserDnPatterns(new String[] {userDnPattern});
        DefaultLdapAuthoritiesPopulator authorities =
                new DefaultLdapAuthoritiesPopulator(contextSource, groupSearchBase);
        authorities.setGroupRoleAttribute("cn");
        authorities.setRolePrefix("ROLE_");
        return new LdapAuthenticationProvider(authenticator, authorities);
    }

    @Bean
    SecurityFilterChain ldapSecurityFilterChain(HttpSecurity http, SecurityResponses responses,
            LdapAuthenticationProvider provider) throws Exception {
        SecurityRules.authorize(http);
        http.authenticationProvider(provider)
                .formLogin(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(responses)
                        .accessDeniedHandler(responses));
        return http.build();
    }
}
