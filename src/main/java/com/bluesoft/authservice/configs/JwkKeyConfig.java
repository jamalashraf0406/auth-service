package com.bluesoft.authservice.configs;

import com.bluesoft.authservice.services.JwkKeyService;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwkKeyConfig {

    @Bean
    public JWKSource<SecurityContext> jwkSource(JwkKeyService jwkKeyService) {
        return (selector, context) -> {
            JWKSet jwkSet = new JWKSet(jwkKeyService.getActiveKey());
            return selector.select(jwkSet);
        };
    }
}