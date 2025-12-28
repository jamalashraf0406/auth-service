package com.example.authservice.utils;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public final class KeyUtil {

    private KeyUtil() {}

    /**
     * Decode RSA Public Key (Base64 URL-safe, no padding)
     */
    public static PublicKey parsePublicKey(String base64Url) throws Exception {
        byte[] decoded = Base64.getUrlDecoder().decode(base64Url);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
        return KeyFactory.getInstance("RSA").generatePublic(spec);
    }

    /**
     * Decode RSA Private Key (Base64 URL-safe, no padding)
     */
    public static PrivateKey parsePrivateKey(String base64Url) throws Exception {
        byte[] decoded = Base64.getUrlDecoder().decode(base64Url);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);
        return KeyFactory.getInstance("RSA").generatePrivate(spec);
    }
}