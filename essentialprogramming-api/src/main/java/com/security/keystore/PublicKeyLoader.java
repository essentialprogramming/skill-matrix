package com.security.keystore;

import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;

public interface PublicKeyLoader {

    /**
     * Return the Public key for a specific provider
     *
     * @param provider Identity provider
     * @param kid keyId
     * @return PublicKey
     */
    PublicKey getPublicKey(String provider, String kid) throws NoSuchAlgorithmException, InvalidKeySpecException;
}
