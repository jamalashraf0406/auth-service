package com.bluesoft.authservice.services;

import com.bluesoft.authservice.entities.JwkKeyEntity;
import com.bluesoft.authservice.repositories.JwkKeyRepository;
import com.bluesoft.authservice.utils.KeyUtil;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.RSAKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.UUID;


@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class JwkKeyService {

    private final JwkKeyRepository jwkKeyRepository;

    /**
     * Active key used ONLY for token signing
     */
    public RSAKey getActiveKey() {
        return jwkKeyRepository.findByActiveTrue()
                .map(this::toRsaKey)
                .orElseGet(this::generateAndSaveKey);
    }

    /**
     * Used by /.well-known/jwks.json
     * Returns public keys (active + old)
     */
    public List<RSAKey> loadAllKeys() {
        return jwkKeyRepository.findAll()
                .stream()
                .map(this::toPublicRsaKey)
                .toList();
    }

    /**
     * Rotate key (call via scheduler)
     */
    public synchronized void rotateKey() {
        jwkKeyRepository.findByActiveTrue()
                .ifPresent(key -> {
                    key.setActive(false);
                    jwkKeyRepository.save(key);
                });

        generateAndSaveKey();
    }

    /**
     * Generate RSA key pair and persist
     */
    private RSAKey generateAndSaveKey() {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048);
            KeyPair keyPair = generator.generateKeyPair();

            String kid = UUID.randomUUID().toString();
            JwkKeyEntity entity = new JwkKeyEntity(
                    kid,
                    encode(keyPair.getPublic().getEncoded()),
                    encode(keyPair.getPrivate().getEncoded()),
                    true,
                    LocalDateTime.now()
            );

            jwkKeyRepository.save(entity);
            return buildSigningRsaKey(kid, keyPair);

        } catch (Exception e) {
            throw new IllegalStateException("Failed to generate RSA JWK", e);
        }
    }

    /**
     * Convert DB entity → RSAKey (PRIVATE + PUBLIC)
     * Used ONLY by auth-service for signing
     */
    private RSAKey toRsaKey(JwkKeyEntity entity) {
        try {
            RSAPublicKey publicKey =
                    (RSAPublicKey) KeyUtil.parsePublicKey(entity.getPublicKey());

            RSAKey.Builder builder = new RSAKey.Builder(publicKey)
                    .keyID(entity.getKeyId())
                    .algorithm(JWSAlgorithm.RS256);

            if (entity.isActive()) {
                RSAPrivateKey privateKey =
                        (RSAPrivateKey) KeyUtil.parsePrivateKey(entity.getPrivateKey());
                builder.privateKey(privateKey);
            }

            return builder.build();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to parse RSA key", e);
        }
    }

    /**
     * Convert DB entity → RSAKey (PUBLIC ONLY)
     * Used for JWKS endpoint
     */
    private RSAKey toPublicRsaKey(JwkKeyEntity entity) {
        try {
            RSAPublicKey publicKey =
                    (RSAPublicKey) KeyUtil.parsePublicKey(entity.getPublicKey());

            return new RSAKey.Builder(publicKey)
                    .keyID(entity.getKeyId())
                    .algorithm(JWSAlgorithm.RS256)
                    .build();

        } catch (Exception e) {
            throw new IllegalStateException("Failed to parse public RSA key", e);
        }
    }

    /**
     * Build signing key from KeyPair
     */
    private RSAKey buildSigningRsaKey(String kid, KeyPair keyPair) {
        return new RSAKey.Builder((RSAPublicKey) keyPair.getPublic())
                .privateKey((RSAPrivateKey) keyPair.getPrivate())
                .keyID(kid)
                .algorithm(JWSAlgorithm.RS256)
                .build();
    }

    /**
     * URL-safe Base64 encoding (JWT compatible)
     */
    private String encode(byte[] value) {
        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(value);
    }

    /**
     * Used during token validation
     */
    public RSAKey findByKeyId(String kid) {
        return jwkKeyRepository.findById(kid)
                .map(this::toRsaKey)
                .orElseThrow(() -> new IllegalArgumentException("Invalid key id: " + kid));
    }
}