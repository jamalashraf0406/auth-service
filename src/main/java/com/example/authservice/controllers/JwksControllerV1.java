package com.example.authservice.controllers;

import com.example.authservice.resources.JwsResourceV1;
import com.nimbusds.jose.jwk.JWKMatcher;
import com.nimbusds.jose.jwk.JWKSelector;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class JwksControllerV1 implements JwsResourceV1 {

    private final JWKSource<SecurityContext> jwkSource;

    public Map<String, Object> keys() throws Exception {
        return new JWKSet(jwkSource.get(new JWKSelector(new JWKMatcher.Builder().build()), null))
                .toJSONObject();
    }
}
