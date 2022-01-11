package com.security.keystore;

import java.security.PublicKey;

public interface PublicKeyLoader {

    /**
     * Return the Public key for a specific provider
     *
     * @param provider Identity provider
     * @param kid keyId
     * @return PublicKey
     */
    PublicKey getPublicKey(String provider, String kid);
}
