package com.example.skye.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Allows Postman clients to authenticate using X-Session-Id header
 * when cookies are not stored automatically.
 */
public class SessionHeaderFilter extends OncePerRequestFilter {

    private static final String SESSION_HEADER = "X-Session-Id";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String sessionId = request.getHeader(SESSION_HEADER);

        if (sessionId != null && !sessionId.isBlank()) {
            HttpServletRequest wrapped = new HttpServletRequestWrapper(request) {
                @Override
                public String getRequestedSessionId() {
                    return sessionId;
                }

                @Override
                public boolean isRequestedSessionIdFromCookie() {
                    return false;
                }

                @Override
                public boolean isRequestedSessionIdFromURL() {
                    return false;
                }
            };
            filterChain.doFilter(wrapped, response);
            return;
        }

        filterChain.doFilter(request, response);
    }
}
