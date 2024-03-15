package com.paj.security;

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

import java.util.Base64;


@ApplicationScoped
public class JWTAuthenticationMechanism implements HttpAuthenticationMechanism {
    private final String LOGIN_URL = "/auth/login";

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

        addTokenCookie(httpServletResponse);
        return httpMessageContext.notifyContainerAboutLogin(validationResult);
    }

    private void addTokenCookie(HttpServletResponse httpServletResponse) {
        var tokenCookie = new Cookie("Token", "this_is_your_token");
        tokenCookie.setHttpOnly(true);

        httpServletResponse.addCookie(tokenCookie);
    }
}
