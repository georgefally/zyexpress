package net.zyexpress.site.auth;

import io.dropwizard.auth.PrincipalImpl;

import javax.annotation.concurrent.Immutable;

@Immutable
public class AuthPrincipal extends PrincipalImpl {
    private final boolean isAdmin;
    private final String token;

    public AuthPrincipal(String name, String token, Boolean isAdmin) {
        super(name);
        this.token = token;
        this.isAdmin = isAdmin;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    String getToken() {
        return token;
    }
}
