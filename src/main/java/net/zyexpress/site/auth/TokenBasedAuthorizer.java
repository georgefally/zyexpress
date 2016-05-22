package net.zyexpress.site.auth;

import com.google.common.base.Objects;
import io.dropwizard.auth.Authorizer;

public class TokenBasedAuthorizer implements Authorizer<AuthPrincipal> {
    @Override
    public boolean authorize(AuthPrincipal principal, String role) {
        if (principal == null) return false;
        if (Objects.equal(role, "ADMIN")) {
            return principal.isAdmin();
        }
        return true;
    }
}

