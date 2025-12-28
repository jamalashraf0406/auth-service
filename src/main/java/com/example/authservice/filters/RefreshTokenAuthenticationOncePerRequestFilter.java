package com.example.authservice.filters;

import com.example.authservice.enums.LoginParam;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
public class RefreshTokenAuthenticationOncePerRequestFilter extends OncePerRequestFilter {

    private final AuthenticationManager authenticationManager;

    public RefreshTokenAuthenticationOncePerRequestFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !LoginParam.REFRESH_TOKEN_URI.getParam().equals(request.getRequestURI())
                || !LoginParam.REFRESH_TOKEN_VALUE.getParam()
                .equals(request.getHeader(LoginParam.REFRESH_TOKEN_HEADER.getParam()));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain chain ) throws IOException, ServletException {

        String authHeader = request.getHeader(LoginParam.AUTHORIZATION.getParam());
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing refresh token");
            return;
        }

        String refreshToken = authHeader.substring(7);

        Authentication authRequest = new RefreshTokenAuthenticationToken(refreshToken);
        Authentication authResult = authenticationManager.authenticate(authRequest);

        SecurityContextHolder.getContext().setAuthentication(authResult);

        response.setContentType("application/json");
        response.getWriter().write("""
            {
              "accessToken": "%s",
              "tokenType": "Bearer"
            }
            """.formatted(authResult.getCredentials())
        );
    }
}

