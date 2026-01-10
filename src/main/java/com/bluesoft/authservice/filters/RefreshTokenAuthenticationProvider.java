package com.bluesoft.authservice.filters;

import com.bluesoft.authservice.entities.RefreshToken;
import com.bluesoft.authservice.services.JwtService;
import com.bluesoft.authservice.services.RefreshTokenService;
import com.bluesoft.authservice.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ott.InvalidOneTimeTokenException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenAuthenticationProvider implements AuthenticationProvider {

    private final RefreshTokenService refreshTokenService;
    private final UserService userDetailsService;
    private final JwtService jwtService;

    @Override
    public Authentication authenticate(Authentication authentication) {

        String refreshToken = (String) authentication.getCredentials();
        String username = null;
        try {
            RefreshToken token = refreshTokenService.validate(refreshToken);
            username = token.getUsername();
        } catch (Exception e) {
            log.error("Error occurred while validating the Refresh token: {}", e.getMessage());
            throw new InvalidOneTimeTokenException(e.getMessage());
        }

        UserDetails user = userDetailsService.loadUserByUsername(username);
        String newAccessToken = jwtService.generateToken(user);

        return new RefreshTokenAuthenticationToken(
                user,
                newAccessToken,
                user.getAuthorities()
        );
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return RefreshTokenAuthenticationToken.class.isAssignableFrom(authentication);
    }

}

