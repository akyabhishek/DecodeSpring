package com.akyabhishek.authdemo.jwt;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {
    private final String bearerToken;
    private final Object principal;

    public JwtAuthenticationToken(String bearerToken) {
        super(Collections.emptyList());
        this.bearerToken = bearerToken;
        this.principal = null;
        setAuthenticated(false);
    }

    public JwtAuthenticationToken(Jwt jwt, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.bearerToken = null;
        this.principal = jwt;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return bearerToken == null ? "" : bearerToken;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    @Override
    public String getName() {
        return principal instanceof Jwt jwt ? jwt.getSubject() : "";
    }
}
