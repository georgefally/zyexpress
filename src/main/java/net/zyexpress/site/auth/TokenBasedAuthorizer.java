package net.zyexpress.site.auth;

import io.dropwizard.auth.Authorizer;

public class TokenBasedAuthorizer implements Authorizer<AuthPrincipal> {
    @Override
    public boolean authorize(AuthPrincipal principal, String role) {
        return principal.getName().equals("username") && role.equals("ADMIN");
    }
}

