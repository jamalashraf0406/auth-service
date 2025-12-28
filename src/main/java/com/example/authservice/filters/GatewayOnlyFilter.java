package com.example.authservice.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Order(0)
public class GatewayOnlyFilter extends OncePerRequestFilter {

    private static final String GATEWAY_HEADER = "X-Gateway-Auth";
    private static final String EXPECTED_VALUE = "gateway-secret-123";

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/auth/") || path.startsWith("/.well-known");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader(GATEWAY_HEADER);

        if (!EXPECTED_VALUE.equals(header)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("Direct Access denied: Not from Gateway");
            return;
        }

        filterChain.doFilter(request, response);
    }
}

