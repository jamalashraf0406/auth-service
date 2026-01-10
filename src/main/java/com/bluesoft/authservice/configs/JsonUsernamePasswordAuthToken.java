package com.bluesoft.authservice.configs;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

// We can use it if we want any extra functionality
public class JsonUsernamePasswordAuthToken extends UsernamePasswordAuthenticationToken {

    public JsonUsernamePasswordAuthToken(String username, String password) {
        super(username, password);
    }
}