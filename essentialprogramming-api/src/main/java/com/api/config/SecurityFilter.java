package com.api.config;

import com.api.model.JWK;
import com.authentication.security.KeyStoreService;
import com.security.AllowUserIf;
import com.security.SecurityChecker;
import com.security.TokenAuthentication;
import com.security.keystore.PublicKeyManager;
import com.token.validation.auth.AuthUtils;
import com.token.validation.jwt.JwtClaims;
import com.token.validation.jwt.JwtUtil;
import com.token.validation.jwt.exception.TokenValidationException;
import com.token.validation.response.ValidationResponse;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.security.core.Authentication;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashSet;
import java.util.Set;

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
            String jwt = AuthUtils.extractBearerToken(authorization);
            String identityProvider = AuthUtils.getClaim(jwt, "identityProvider");
            String keyId = JwtUtil.getKeyId(jwt);

            publicKey = publicKeyManager.getIdentityProviderPublicKey(identityProvider, keyId);

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

            if (method.isAnnotationPresent(AllowUserIf.class)) {
                AllowUserIf permissionsAnnotation = method.getAnnotation(AllowUserIf.class);
                String securityExpression = permissionsAnnotation.value();
                Authentication authentication = createAuthentication(response.getClaims());

                //Has user required authorities ?
                if (!SecurityChecker.hasAuthorities(authentication, securityExpression)) {
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

    private static Authentication createAuthentication(JwtClaims claims) {
        return new TokenAuthentication(claims);
    }
}