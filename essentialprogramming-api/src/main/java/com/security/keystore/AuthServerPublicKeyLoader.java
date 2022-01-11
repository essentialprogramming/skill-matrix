package com.security.keystore;

import com.api.model.AuthServicePublicKey;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.concurrent.ConcurrentHashMap;

public class AuthServerPublicKeyLoader{
    private final ConcurrentHashMap<String, PublicKey> cache = new ConcurrentHashMap<>();

    private AuthServerPublicKeyLoader() {
    }

    private static class AuthServerPublicKeyHolder {
        static final AuthServerPublicKeyLoader INSTANCE = new AuthServerPublicKeyLoader();
    }

    public static AuthServerPublicKeyLoader getInstance() {
        return AuthServerPublicKeyHolder.INSTANCE;
    }

    public PublicKey getPublicKey(String kid) {
        if (kid != null && cache.containsKey(kid)){
            return cache.get(kid);
        }
        return null;
    }


    private PublicKey getPublicKey(AuthServicePublicKey key)
            throws NoSuchAlgorithmException, InvalidKeySpecException {

        KeyFactory keyFactory = KeyFactory.getInstance(key.getKeyType());
        BigInteger modulus = new BigInteger(1, Base64.getUrlDecoder().decode(key.getModulus()));
        BigInteger publicExponent = new BigInteger(1, Base64.getUrlDecoder().decode(key.getExponent()));
        return keyFactory.generatePublic(new RSAPublicKeySpec(modulus, publicExponent));
    }
}
