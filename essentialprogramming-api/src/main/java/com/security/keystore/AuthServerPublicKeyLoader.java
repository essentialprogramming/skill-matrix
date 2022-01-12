package com.security.keystore;

import com.api.env.resources.AppResources;
import com.api.model.JWK;
import com.authentication.exceptions.codes.ErrorCode;
import com.util.cloud.Environment;
import com.util.exceptions.ServiceException;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.concurrent.ConcurrentHashMap;

public class AuthServerPublicKeyLoader {
    private final ConcurrentHashMap<String, PublicKey> cache = new ConcurrentHashMap<>();

    private AuthServerPublicKeyLoader() {
    }

    private static class AuthServerPublicKeyHolder {
        static final AuthServerPublicKeyLoader INSTANCE = new AuthServerPublicKeyLoader();
    }

    public static AuthServerPublicKeyLoader getInstance() {
        return AuthServerPublicKeyHolder.INSTANCE;
    }

    public PublicKey getPublicKey(String kid) throws NoSuchAlgorithmException, InvalidKeySpecException {
        if (kid != null) {
            if (cache.containsKey(kid)) {
                return cache.get(kid);
            }

            JWK jwk = getAuthServerJWK();
            PublicKey publicKey = getPublicKey(jwk);
            cache.put(kid, publicKey);
            return publicKey;
        }
        throw new ServiceException(ErrorCode.UNABLE_TO_GET_KEY_ID_FROM_TOKEN);
    }

    private JWK getAuthServerJWK() {
        //call endpoint from auth-server to get JWK;
        Client client = ClientBuilder.newClient();

        final String url = Environment.getProperty("appip", "localhost");
        final String port = AppResources.AUTH_PORT.value();

        WebTarget webTarget = client.target("http://" + url + ":" + port + "/api");

        WebTarget publicKeyWebTarget = webTarget.path("/auth/oauth2/jwks");

        Invocation.Builder invocationBuilder = publicKeyWebTarget.request(MediaType.APPLICATION_JSON)
                .header("Accept-Language", "en-US");

        JWK jwk = invocationBuilder.get(JWK.class);

        return jwk;
    }


    private PublicKey getPublicKey(JWK key)
            throws NoSuchAlgorithmException, InvalidKeySpecException {

        KeyFactory keyFactory = KeyFactory.getInstance(key.getKeyType());
        BigInteger modulus = new BigInteger(1, Base64.getUrlDecoder().decode(key.getModulus()));
        BigInteger publicExponent = new BigInteger(1, Base64.getUrlDecoder().decode(key.getExponent()));
        return keyFactory.generatePublic(new RSAPublicKeySpec(modulus, publicExponent));
    }

}
