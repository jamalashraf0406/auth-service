package com.example.authservice.filters;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Slf4j
public class RefreshTokenAuthenticationToken extends AbstractAuthenticationToken {

    private final Object principal;
    private final String token;

    // unauthenticated
    public RefreshTokenAuthenticationToken(String refreshToken) {
        super(null);
        this.principal = null;
        this.token = refreshToken;
        setAuthenticated(false);
    }

    // authenticated
    public RefreshTokenAuthenticationToken(Object principal, String accessToken,
            Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.token = accessToken;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return token;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

}

