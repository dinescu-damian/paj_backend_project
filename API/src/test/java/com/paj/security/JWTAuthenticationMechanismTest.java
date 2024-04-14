package com.paj.security;

import jakarta.security.enterprise.AuthenticationException;
import jakarta.security.enterprise.AuthenticationStatus;
import jakarta.security.enterprise.authentication.mechanism.http.HttpMessageContext;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class JWTAuthenticationMechanismTest {

    private final JWTAuthenticationMechanism authenticationMechanism = new JWTAuthenticationMechanism();

    // Mock the required classes
    private final HttpServletRequest request = mock(HttpServletRequest.class);
    private final HttpServletResponse response = mock(HttpServletResponse.class);
    private final HttpMessageContext messageContext = mock(HttpMessageContext.class);

    private final AuthenticationStatus unauthorizedReturnValue = AuthenticationStatus.SEND_FAILURE;
    private final AuthenticationStatus authorizedReturnValue = AuthenticationStatus.SUCCESS;

    // JWT with 20 years validity
    private final String TestJWT = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJQQUotUGF5YXJhLVNlcnZlciIsInN1YiI6IlVzZXIxIiwiaWF0IjoxNzExMTAwOTMzLCJleHAiOjIzMTExMDA5MzN9.HW5Smur-9ga7BliHBwuN_ZGx4aXodtW-sVnkRXGgvj4";

    {
        // The return value doesn't matter, we just need to check that responseUnauthorized is called
        when(messageContext.responseUnauthorized()).thenReturn(unauthorizedReturnValue);
        when(messageContext.notifyContainerAboutLogin(any())).thenReturn(authorizedReturnValue);

        // Provide an identity store
        authenticationMechanism.identityStore = new MockIdentityStore();
    }

    @Test
    void validateRequest_ShouldReturnUnauthorizedIfURLNotLoginAndNoCookiesPresent() throws AuthenticationException {
        // Mock the return values for the needed methods
        when(request.getPathInfo())
                .thenReturn("/"); // Any path besides the login one

        // Null cookie list
        when(request.getCookies()).thenReturn(null);
        assertEquals(unauthorizedReturnValue,
                authenticationMechanism.validateRequest(request, response, messageContext));

        // Non-null empty cookie list
        when(request.getCookies()).thenReturn(new Cookie[]{});
        assertEquals(unauthorizedReturnValue,
                authenticationMechanism.validateRequest(request, response, messageContext));
    }

    @Test
    void validateRequest_ShouldReturnUnauthorizedIfURLIsLoginAndMethodIsPostAndBasicAuthorizationHeaderNotPresentOrWrong() throws AuthenticationException {
        // Mock the return values for the needed methods
        when(request.getPathInfo()).thenReturn("/auth/login");
        when(request.getMethod()).thenReturn("POST");

        // Authorization header does not exist
        when(request.getHeader("Authorization")).thenReturn(null);
        assertEquals(unauthorizedReturnValue,
                authenticationMechanism.validateRequest(request, response, messageContext));

        // Authorization header has wrong format
        when(request.getHeader("Authorization")).thenReturn("Wrong Format");
        assertEquals(unauthorizedReturnValue,
                authenticationMechanism.validateRequest(request, response, messageContext));
    }

    @Test
    void validateRequest_ShouldReturnUnauthorizedIfURLIsLoginAndMethodIsPostAndWrongCredentialsInBasicAuthorizationHeader() throws AuthenticationException {
        // Mock the return values for the needed methods
        when(request.getPathInfo()).thenReturn("/auth/login");
        when(request.getMethod()).thenReturn("POST");

        // Authorization header exists and the credentials are wrong
        when(request.getHeader("Authorization")).thenReturn(
                "Basic " + Base64.getEncoder().encodeToString("wrong:credentials".getBytes())
        );

        assertEquals(unauthorizedReturnValue,
                authenticationMechanism.validateRequest(request, response, messageContext));
    }

    @Test
    void validateRequest_ShouldReturnAuthorizedAndTokenCookieIfURLIsLoginAndMethodIsPostAndCorrectCredentialsInBasicAuthorizationHeader() throws AuthenticationException {
        // Mock the return values for the needed methods
        when(request.getPathInfo()).thenReturn("/auth/login");
        when(request.getMethod()).thenReturn("POST");

        // Authorization header exists and the credentials are correct
        when(request.getHeader("Authorization")).thenReturn(
                "Basic " + Base64.getEncoder().encodeToString("User1:pass1".getBytes())
        );

        assertEquals(authorizedReturnValue,
                authenticationMechanism.validateRequest(request, response, messageContext));

        // Capture the cookie
        ArgumentCaptor<Cookie> cookieArg = ArgumentCaptor.forClass(Cookie.class);
        verify(response).addCookie(cookieArg.capture());

        // Check that a cookie with the name Token was added and that it has a value
        assertEquals("Token", cookieArg.getValue().getName());
        assertNotNull(cookieArg.getValue().getValue());
    }

    @Test
    void validateRequest_ShouldReturnAuthorizedIfURLNotLoginAndValidTokenInCookies() throws AuthenticationException {
        // Mock the return values for the needed methods
        when(request.getPathInfo()).thenReturn("/");

        // Authorization header exists and the credentials are correct
        var tokenCookie = new Cookie("Token", TestJWT);
        when(request.getCookies()).thenReturn(new Cookie[]{tokenCookie});

        assertEquals(authorizedReturnValue,
                authenticationMechanism.validateRequest(request, response, messageContext));
    }

    @Test
    void validateRequest_ShouldReturnUnauthorizedIfURLNotLoginAndInvalidTokenInCookies() throws AuthenticationException {
        // Mock the return values for the needed methods
        when(request.getPathInfo()).thenReturn("/");

        // Authorization header exists and the credentials are correct
        var tokenCookie = new Cookie("Token", "InvalidToken");
        when(request.getCookies()).thenReturn(new Cookie[]{tokenCookie});

        assertEquals(unauthorizedReturnValue,
                authenticationMechanism.validateRequest(request, response, messageContext));
    }
}