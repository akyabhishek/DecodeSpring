package com.akyabhishek.authdemo.apikey;

import java.io.IOException;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {
    public static final String HEADER = "X-API-KEY";

    private final AuthenticationManager authenticationManager;
    private final AuthenticationEntryPoint entryPoint;

    public ApiKeyAuthenticationFilter(AuthenticationManager authenticationManager, AuthenticationEntryPoint entryPoint) {
        this.authenticationManager = authenticationManager;
        this.entryPoint = entryPoint;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String key = request.getHeader(HEADER);
        if (key != null) {
            try {
                Authentication result = authenticationManager.authenticate(new ApiKeyAuthenticationToken(key));
                SecurityContext context = SecurityContextHolder.createEmptyContext();
                context.setAuthentication(result);
                SecurityContextHolder.setContext(context);
            } catch (AuthenticationException exception) {
                SecurityContextHolder.clearContext();
                entryPoint.commence(request, response, exception);
                return;
            }
        }
        chain.doFilter(request, response);
    }
}
