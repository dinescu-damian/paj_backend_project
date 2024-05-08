package com.paj.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.annotation.security.DeclareRoles;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.security.enterprise.AuthenticationStatus;
import jakarta.security.enterprise.SecurityContext;
import jakarta.security.enterprise.authentication.mechanism.http.HttpAuthenticationMechanism;
import jakarta.security.enterprise.authentication.mechanism.http.HttpMessageContext;
import jakarta.security.enterprise.credential.UsernamePasswordCredential;
import jakarta.security.enterprise.identitystore.CredentialValidationResult;
import jakarta.security.enterprise.identitystore.IdentityStoreHandler;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.HttpMethod;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.*;

@ApplicationScoped
@DeclareRoles({"USER", "GUEST"})
public class JWTAuthenticationMechanism implements HttpAuthenticationMechanism {
    private final String LOGIN_URL = "/auth/login";
    private final String REGISTER_URL = "/auth/register";
    private final String JWT_SECRET_FILE_LOCATION = "/jwt.sercret";
    private final String JWT_COOKIE_NAME = "Token";
    private final String JWT_ROLES_CLAIM = "roles";
    private final String JWT_ISSUER = "PAJ-Payara-Server";
    private final long JWT_VALIDITY_IN_MILISECONDS = (10 * 60 * 1000); // 10 minutes

    @Inject
    IdentityStoreHandler identityStore;

    @Override
    public AuthenticationStatus validateRequest(HttpServletRequest httpServletRequest,
                                                HttpServletResponse httpServletResponse,
                                                HttpMessageContext httpMessageContext) {
        // If the user attempts to create an account, pass the request forward
        if(httpServletRequest.getPathInfo().equals(REGISTER_URL) && httpServletRequest.getMethod().equals(HttpMethod.POST))
            return httpMessageContext.doNothing();

        // If the user is accessing the login URL, perform the basic authentication using username and password
        // and if correct credentials were provided, set a HttpOnly cookie containing the JWT
        if(httpServletRequest.getPathInfo().equals(LOGIN_URL) && httpServletRequest.getMethod().equals(HttpMethod.POST))
            return usernameAndPasswordLogin(httpServletRequest, httpServletResponse, httpMessageContext);

        // Otherwise, check that the request is coming with a valid JWT cookie
        return validateToken(httpServletRequest, httpMessageContext);
    }

    private AuthenticationStatus usernameAndPasswordLogin(HttpServletRequest httpServletRequest,
                                                          HttpServletResponse httpServletResponse,
                                                          HttpMessageContext httpMessageContext) {
        // Check that the authorization header exists
        var authHeader = httpServletRequest.getHeader("Authorization");
        if(authHeader == null)
            return httpMessageContext.responseUnauthorized();

        // Parse the authentication header and get the username and password
        authHeader = authHeader.replace("Basic ", "");
        String[] usernameAndPassword;
        try {
            usernameAndPassword = new String(Base64.getDecoder().decode(authHeader))
                    .split(":");

            // If the split resulted in any number of parts different to 2, return unauthorized
            if(usernameAndPassword.length != 2)
                return httpMessageContext.responseUnauthorized();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return httpMessageContext.responseUnauthorized();
        }

        // Validate the username and password using the identity store
        var validationResult = identityStore.validate(
                new UsernamePasswordCredential(usernameAndPassword[0], usernameAndPassword[1])
        );

        // Return a response based on the validity of the provided credentials
        if(validationResult == CredentialValidationResult.INVALID_RESULT)
            return httpMessageContext.responseUnauthorized();

        addTokenCookie(httpServletResponse, validationResult);
        return httpMessageContext.notifyContainerAboutLogin(validationResult);
    }

    private AuthenticationStatus validateToken(HttpServletRequest httpServletRequest,
                                               HttpMessageContext httpMessageContext) {
        // Used if the token is not present
        var guestCredentials = new CredentialValidationResult(
                "Guest", new HashSet<>(Collections.singletonList("GUEST"))
        );
        var cookies = httpServletRequest.getCookies();

        // If no cookies are found, respond with unauthorized
        if(cookies == null)
            return httpMessageContext.notifyContainerAboutLogin(guestCredentials);

        // Get the token cookie
        var tokenCookie = Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals(JWT_COOKIE_NAME))
                .findFirst();

        // If no token cookie is present, authorize the user as a guest
        if(tokenCookie.isEmpty())
            return httpMessageContext.notifyContainerAboutLogin(guestCredentials);

        DecodedJWT token;
        try {
            token = JWT.decode(tokenCookie.get().getValue());
            // Verify that the token has not been modified by checking that the signature matches
            var algorithm = Algorithm.HMAC256(getKeyString());
            JWT.require(algorithm)
                    .withIssuer(JWT_ISSUER)
                    .withClaimPresence("sub")
                    .withClaimPresence(JWT_ROLES_CLAIM)
                    .build()
                    .verify(token);
        } catch (JWTVerificationException e) {
            // Invalid token
            return httpMessageContext.responseUnauthorized();
        }

        return httpMessageContext.notifyContainerAboutLogin(
                new CredentialValidationResult(
                    token.getSubject(), new HashSet<>(token.getClaim(JWT_ROLES_CLAIM).asList(String.class))
                )
        );
    }

    // Create the jwt and add it in a HttpOnly cookie
    private void addTokenCookie(HttpServletResponse httpServletResponse,
                                CredentialValidationResult validationResult) {
        var algorithm = Algorithm.HMAC256(getKeyString());
        var expiration = System.currentTimeMillis() + JWT_VALIDITY_IN_MILISECONDS;
        var token = JWT.create()
                .withIssuer(JWT_ISSUER)
                .withSubject(validationResult.getCallerPrincipal().getName())
                .withArrayClaim(JWT_ROLES_CLAIM, validationResult.getCallerGroups().toArray(new String[0]))
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(expiration))
                .sign(algorithm);

        var tokenCookie = new Cookie(JWT_COOKIE_NAME, token);
        tokenCookie.setHttpOnly(true);
        tokenCookie.setMaxAge((int) (JWT_VALIDITY_IN_MILISECONDS/1000));
        tokenCookie.setPath("/");

        httpServletResponse.addCookie(tokenCookie);
    }

    // Read the secret key from a file
    private String getKeyString(){
        try (var inputStream = getClass().getResourceAsStream(JWT_SECRET_FILE_LOCATION);
             InputStreamReader inputStreamReader = new InputStreamReader(Objects.requireNonNull(inputStream));
             BufferedReader bufferedReader = new BufferedReader(inputStreamReader)
        ){
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null)
                result.append(line).append("\n");

            return result.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
