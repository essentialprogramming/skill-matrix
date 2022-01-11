package com.api.config;

import com.api.env.resources.AppResources;
import com.api.model.AuthServicePublicKey;
import com.authentication.exceptions.codes.ErrorCode;
import com.authentication.security.KeyStoreService;
import com.security.keystore.PublicKeyManager;
import com.token.validation.auth.AuthUtils;
import com.token.validation.jwt.JwtClaims;
import com.token.validation.jwt.JwtUtil;
import com.token.validation.jwt.exception.TokenValidationException;
import com.token.validation.response.ValidationResponse;
import com.util.cloud.Environment;
import com.util.exceptions.ServiceException;
import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.*;

public class SecurityFilter implements ContainerRequestFilter {

    private final PublicKeyManager publicKeyManager;
    private final KeyStoreService keyStoreService;
    private final ResourceInfo resourceInfo;

    public SecurityFilter(PublicKeyManager publicKeyManager, KeyStoreService keyStoreService, ResourceInfo resourceInfo) {
        this.publicKeyManager = publicKeyManager;
        this.keyStoreService = keyStoreService;
        this.resourceInfo = resourceInfo;
    }


    @Override
    public void filter(final ContainerRequestContext requestContext) {
        final String authorization = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
        if (authorization == null) {
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity("{\"error\":\"missing_authorization_header\"}").build());
            return;
        }
        if (!authorization.startsWith("Bearer ")) {
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity("{\"error\":\"invalid_authorization_scheme\"}").build());
            return;
        }

        final PublicKey publicKey;
        try {
            //
            String jwt = AuthUtils.extractBearerToken(authorization);
            String identityProvider = AuthUtils.getClaim(jwt, "identityProvider") ;
            String keyId = JwtUtil.getKeyId(jwt);

            switch (Objects.requireNonNull(identityProvider)) {
                case "authorization~server":
                   //call method from auth-server;
                    Client client = ClientBuilder.newClient();

                    final String url = Environment.getProperty("appip", "localhost");
                    final String port = AppResources.AUTH_PORT.value();

                    WebTarget webTarget = client.target("http://" + url + ":" + port + "/api");

                    WebTarget publicKeyWebTarget = webTarget.path("/auth/publicKey");

                    Invocation.Builder invocationBuilder = publicKeyWebTarget.request(MediaType.APPLICATION_JSON)
                            .header("Accept-Language", "en-US");

                    AuthServicePublicKey authServicePublicKey = invocationBuilder.get(AuthServicePublicKey.class);

                    publicKey = getPublicKey(authServicePublicKey);

                    break;
                case "skill-matrix":
                     publicKey = keyStoreService.getPublicKey();
                    break;
                default:
                    throw new ServiceException(ErrorCode.IDENTITY_PROVIDER_UNRECOGNIZED,
                            "No PublicKeyLoader found for idp " + identityProvider);
            }

            ValidationResponse<JwtClaims> response = JwtUtil.verifyJwt(AuthUtils.extractBearerToken(authorization), publicKey);
            if (!response.isValid()) {
                requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity("{\"error\":\"invalid_credentials\"}").build());
                return;
            }

            Method method = resourceInfo.getResourceMethod();
            //Verify user access
            if (method.isAnnotationPresent(RolesAllowed.class)) {
                RolesAllowed rolesAnnotation = method.getAnnotation(RolesAllowed.class);
                Set<String> rolesSet = new HashSet<>(Arrays.asList(rolesAnnotation.value()));

                //Is user valid?
                if (!isUserAllowed(response.getClaims(), rolesSet)) {
                    requestContext.abortWith(Response.status(Response.Status.FORBIDDEN).build());

                }
            }
        } catch (TokenValidationException exception) {
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity("{\"error\":\"invalid_token_format\"}").build());
        } catch (Exception exception) {
            requestContext.abortWith(Response.status(Response.Status.SERVICE_UNAVAILABLE).entity("{\"error\":\"Unable to process your request, due to: \n" + ExceptionUtils.getStackTrace(exception) + "\n\"}").build());
        }
    }

    public static boolean isUserAllowed(JwtClaims claims, final Set<String> rolesSet) {
        boolean isAllowed = false;

        String[] roles = claims.getRoles().split("\\,");

        if (rolesSet.stream().anyMatch(Arrays.asList(roles)::contains)) {
            isAllowed = true;
        }
        return isAllowed;
    }


    private PublicKey getPublicKey(AuthServicePublicKey key)
            throws NoSuchAlgorithmException, InvalidKeySpecException {

        KeyFactory keyFactory = KeyFactory.getInstance(key.getKeyType());
        BigInteger modulus = new BigInteger(1, Base64.getUrlDecoder().decode(key.getModulus()));
        BigInteger publicExponent = new BigInteger(1, Base64.getUrlDecoder().decode(key.getExponent()));
        return keyFactory.generatePublic(new RSAPublicKeySpec(modulus, publicExponent));
    }
}