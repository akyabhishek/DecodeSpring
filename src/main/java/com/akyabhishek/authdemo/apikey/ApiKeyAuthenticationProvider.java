package com.akyabhishek.authdemo.apikey;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

@Component
@Profile("api-key")
public class ApiKeyAuthenticationProvider implements AuthenticationProvider {
    private final byte[] expectedKey;

    public ApiKeyAuthenticationProvider(@Value("${auth-demo.api-key}") String expectedKey) {
        this.expectedKey = expectedKey.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        byte[] presented = String.valueOf(authentication.getCredentials()).getBytes(StandardCharsets.UTF_8);
        if (!MessageDigest.isEqual(expectedKey, presented)) {
            throw new BadCredentialsException("Invalid API key");
        }
        return new ApiKeyAuthenticationToken("api-key-client",
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return ApiKeyAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
