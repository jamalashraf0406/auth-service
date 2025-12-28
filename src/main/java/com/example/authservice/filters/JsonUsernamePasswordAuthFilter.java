package com.example.authservice.filters;

import com.example.authservice.enums.LoginParam;
import com.example.authservice.handlers.JwtAuthFailureHandler;
import com.example.authservice.handlers.JwtAuthSuccessHandler;
import com.example.authservice.requests.LoginRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * This filter is used to validate and login the user based on json object provided with credential
 * This filter responsible to parse the json object and create login request object.
 * */
@Slf4j
@Component
public class JsonUsernamePasswordAuthFilter extends UsernamePasswordAuthenticationFilter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public JsonUsernamePasswordAuthFilter(AuthenticationManager authManager,
        JwtAuthSuccessHandler successHandler, JwtAuthFailureHandler failureHandler) {
        setAuthenticationManager(authManager);
        setAuthenticationSuccessHandler(successHandler);
        setAuthenticationFailureHandler(failureHandler);
        setFilterProcessesUrl(LoginParam.LOGIN_URI.getParam()); // login endpoint
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {

        if (!request.getContentType().startsWith(MediaType.APPLICATION_JSON_VALUE)) {
            throw new AuthenticationServiceException(
                    "Content-Type must be application/json");
        }

        try {
            LoginRequest loginRequest = objectMapper.readValue(request.getInputStream(), LoginRequest.class);

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    loginRequest.username(),
                    loginRequest.password()
            );

            setDetails(request, authToken);
            return this.getAuthenticationManager().authenticate(authToken);

        } catch (IOException e) {
            throw new AuthenticationServiceException("Invalid login request", e);
        }
    }
}
