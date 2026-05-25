package com.example.skye.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Keeps HTTP sessions alive during long E2E test runs (e.g. on Render).
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 15)
public class SessionRefreshFilter extends OncePerRequestFilter {

    private static final int SESSION_MAX_INACTIVE_SECONDS = 86400;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.setMaxInactiveInterval(SESSION_MAX_INACTIVE_SECONDS);
        }
        filterChain.doFilter(request, response);
    }
}
