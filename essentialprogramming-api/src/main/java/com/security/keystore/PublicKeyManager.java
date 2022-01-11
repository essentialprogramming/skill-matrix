package com.security.keystore;

import com.authentication.exceptions.codes.ErrorCode;
import com.authentication.security.KeyStoreService;
import com.util.exceptions.ServiceException;

import java.security.PublicKey;

public class PublicKeyManager {
    private static class PublicKeyManagerHolder {
        static final PublicKeyManager INSTANCE = new PublicKeyManager();
    }

    public static PublicKeyManager getInstance() {
        return PublicKeyManagerHolder.INSTANCE;
    }

    private final PublicKeyLoader publicKeyLoader = (identityProvider, kid) -> {

        switch (identityProvider) {
            case "authorization~server":
                return AuthServerPublicKeyLoader.getInstance().getPublicKey(kid);
            case "skill-matrix":
                return KeyStoreService.getInstance().getPublicKey();
            default:
                throw new ServiceException(ErrorCode.IDENTITY_PROVIDER_UNRECOGNIZED,
                        "No PublicKeyLoader found for idp " + identityProvider);
        }
    };


    public PublicKey getIdentityProviderPublicKey(String identityProvider, String kid) {
        return publicKeyLoader.getPublicKey(identityProvider, kid);
    }
}
