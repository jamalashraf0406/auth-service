package com.example.authservice.services;

import com.example.authservice.utils.ObjectMapperUtil;
import com.nimbusds.jose.jwk.RSAKey;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Date;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwkKeyService jwkKeyService;

    public String generateToken(UserDetails user) {

        RSAKey rsaKey = jwkKeyService.getActiveKey();

        try {
            return Jwts.builder()
                    .setSubject(user.getUsername())
                    .claim("roles", user.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority)
                            .toList())
                    .setIssuedAt(new Date())
                    .setIssuer("http://auth-service")
                    .setExpiration(new Date(System.currentTimeMillis() + 15 * 60 * 1000))
                    .setHeaderParam("kid", rsaKey.getKeyID())
                    .signWith(rsaKey.toPrivateKey(), SignatureAlgorithm.RS256)
                    .compact();
        } catch (Exception e) {
            throw new IllegalStateException("JWT generation failed", e);
        }
    }

    public Claims extractAllClaims(String token) {

        String kid = extractKid(token);
        RSAKey rsaKey = jwkKeyService.findByKeyId(kid);

        try {
            return Jwts.parserBuilder()
                    .setSigningKey(rsaKey.toPublicKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            log.error("Error occurred while checking the details of the token validation: {}", e.getMessage());
            throw new SecurityException("Invalid JWT token", e);
        }
    }

    private String extractKid(String token) {
        try {
            String header = token.split("\\.")[0];
            byte[] decodedHeader = Base64.getUrlDecoder().decode(header);

            Map<String, Object> headerMap =
                    ObjectMapperUtil.OBJECT_MAPPER.readValue(decodedHeader, Map.class);

            return (String) headerMap.get("kid");
        } catch (Exception e) {
            log.error("Invalid JWT token Header found: {}", e.getMessage());
            throw new IllegalArgumentException("Invalid JWT header", e);
        }
    }

    public boolean isTokenValid(String token) {
        try {
            extractAllClaims(token);
            return true;
        } catch (Exception e) {
            log.error("Error occurred while check validity of the token: {}",e.getMessage());
            return false;
        }
    }
}

