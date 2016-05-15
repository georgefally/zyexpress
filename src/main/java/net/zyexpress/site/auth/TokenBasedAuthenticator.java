package net.zyexpress.site.auth;

import com.google.common.base.Objects;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.basic.BasicCredentials;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

// authenticator based on a token issued by a login restful API
// it works like below:
// 1, frontend asks for a username/password from user
// 2, frontend uses /restful/user/login to get a one-time token - the password is hash-stretched
// 3, for each subsequent request, frontend will send HTTP basic authentication header, where the password
//    is the one-time token
//          "Authorization": "Basic " + btoa(USERNAME + ":" + PASSWORD)
public class TokenBasedAuthenticator implements Authenticator<BasicCredentials, AuthPrincipal> {

    private static final Cache<Pair<String, String>, AuthPrincipal> principalCache = CacheBuilder.newBuilder()
            .maximumSize(10000).expireAfterWrite(30, TimeUnit.MINUTES).build();

    public static String getUniqueToken() {
        return UUID.randomUUID().toString();
    }
    public static void addPrincipalToCache(final String token, final AuthPrincipal principal) {
        principalCache.put(Pair.of(principal.getName(), token), principal);
    }

    @Override
    public Optional<AuthPrincipal> authenticate(BasicCredentials credentials)
            throws AuthenticationException {
        String userName = credentials.getUsername();
        String token = credentials.getPassword();

        AuthPrincipal principal = principalCache.getIfPresent(Pair.of(userName, token));
        if (principal == null) return Optional.empty();
        if (!Objects.equal(principal.getToken(), token)) return Optional.empty();
        if (!Objects.equal(principal.getName(), userName)) return Optional.empty();

        return Optional.of(principal);
    }
}
