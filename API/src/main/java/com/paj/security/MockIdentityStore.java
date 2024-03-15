package com.paj.security;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.security.enterprise.credential.UsernamePasswordCredential;
import jakarta.security.enterprise.identitystore.CredentialValidationResult;
import jakarta.security.enterprise.identitystore.IdentityStore;

import java.util.HashMap;
import java.util.Set;

@ApplicationScoped
public class MockIdentityStore implements IdentityStore {

    HashMap<String, UsernamePasswordCredential> validUsers = new HashMap<>();
    {
        validUsers.put("User1", new UsernamePasswordCredential("User1", "pass1"));
        validUsers.put("User2", new UsernamePasswordCredential("User2", "pass2"));
        validUsers.put("User3", new UsernamePasswordCredential("User3", "pass3"));
        validUsers.put("User4", new UsernamePasswordCredential("User4", "pass4"));
    }

    public CredentialValidationResult validate(UsernamePasswordCredential credential) {
        var existingUser = validUsers.get(credential.getCaller());
        if(existingUser != null && existingUser.getPassword().compareTo(credential.getPasswordAsString()))
            return new CredentialValidationResult(existingUser.getCaller());

        return CredentialValidationResult.INVALID_RESULT;
    }

    @Override
    public Set<ValidationType> validationTypes() {
        return Set.of(ValidationType.VALIDATE);
    }
}
