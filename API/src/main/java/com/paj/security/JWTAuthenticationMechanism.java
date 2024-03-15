package com.paj.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.security.enterprise.AuthenticationException;
import jakarta.security.enterprise.AuthenticationStatus;
import jakarta.security.enterprise.authentication.mechanism.http.HttpAuthenticationMechanism;
import jakarta.security.enterprise.authentication.mechanism.http.HttpMessageContext;
import jakarta.security.enterprise.credential.UsernamePasswordCredential;
import jakarta.security.enterprise.identitystore.CredentialValidationResult;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.Base64;
import java.util.Date;
import java.util.Objects;


@ApplicationScoped
public class JWTAuthenticationMechanism implements HttpAuthenticationMechanism {
    private final String LOGIN_URL = "/auth/login";
    private final String JWT_SECRET_FILE_LOCATION = "/jwt.sercret";
    private final long JWT_VALIDITY_IN_MILISECONDS = (10 * 60 * 1000); // 10 minutes

    @Inject
    MockIdentityStore identityStore;

    @Override
    public AuthenticationStatus validateRequest(HttpServletRequest httpServletRequest,
                                                HttpServletResponse httpServletResponse,
                                                HttpMessageContext httpMessageContext) throws AuthenticationException {
        // If the user is accessing the login URL, perform the basic authentication using username and password
        // and if correct credentials were provided, set a HttpOnly cookie containing the JWT
        if(httpServletRequest.getPathInfo().equals(LOGIN_URL))
            return usernameAndPasswordLogin(httpServletRequest, httpServletResponse, httpMessageContext);

        var cookies = httpServletRequest.getCookies();
        return httpMessageContext.doNothing();
    }

    private AuthenticationStatus usernameAndPasswordLogin(HttpServletRequest httpServletRequest,
                                                          HttpServletResponse httpServletResponse,
                                                          HttpMessageContext httpMessageContext) {
        // Parse the authentication header and get the username and password
        var authHeader = httpServletRequest.getHeader("Authorization")
                .replace("Basic ", "");
        var usernameAndPassword = new String(Base64.getDecoder().decode(authHeader))
                .split(":");

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

    private void addTokenCookie(HttpServletResponse httpServletResponse,
                                CredentialValidationResult validationResult) {
        var algorithm = Algorithm.HMAC256(getKeyString());
        var expiration = System.currentTimeMillis() + JWT_VALIDITY_IN_MILISECONDS;
        var token = JWT.create()
                .withIssuer("PAJ-Payara-Server")
                .withSubject(validationResult.getCallerPrincipal().getName())
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(expiration))
                .sign(algorithm);

        var tokenCookie = new Cookie("Token", token);
        tokenCookie.setHttpOnly(true);
        tokenCookie.setMaxAge((int) (JWT_VALIDITY_IN_MILISECONDS/1000));

        httpServletResponse.addCookie(tokenCookie);
    }

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
