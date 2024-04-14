package com.paj.security;

import com.paj.api.services.UserService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.security.enterprise.credential.Credential;
import jakarta.security.enterprise.credential.UsernamePasswordCredential;
import jakarta.security.enterprise.identitystore.CredentialValidationResult;
import jakarta.security.enterprise.identitystore.IdentityStore;
import jakarta.security.enterprise.identitystore.Pbkdf2PasswordHash;

@ApplicationScoped
public class DbIdentityStore implements IdentityStore {
    @Inject
    UserService userService;

    @Inject
    Pbkdf2PasswordHash hasher;

    public CredentialValidationResult validateUsernamePassCredentials(UsernamePasswordCredential credential) {
        var existingUser = userService.findUserByEmail(credential.getCaller());
        if(existingUser != null && hasher.verify(credential.getPasswordAsString().toCharArray(), existingUser.getPassword()))
            return new CredentialValidationResult(existingUser.getEmail());

        return CredentialValidationResult.INVALID_RESULT;
    }

    @Override
    public CredentialValidationResult validate(Credential credential) {
        return validateUsernamePassCredentials((UsernamePasswordCredential) credential);
    }
}
