package org.canopyplatform.canopy.searchservice.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Resolves an inbound JWT into a {@link SearchAccessContext}. Null / unknown
 * JWTs degrade gracefully to {@link SearchAccessContext#anonymous()} —
 * the search endpoint is permitAll'd, so anonymous callers are normal.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SearchAccessContextResolver {

    private final AuthUserRepository authUserRepository;

    public SearchAccessContext resolve(Jwt jwt) {
        if (jwt == null) {
            return SearchAccessContext.anonymous();
        }
        String uuid = jwt.getClaimAsString("sub");
        if (uuid == null) {
            return SearchAccessContext.anonymous();
        }
        Optional<AuthUser> user = authUserRepository.findByUuid(uuid);
        if (user.isEmpty()) {
            // JWT references a Keycloak UUID that doesn't have a Canopy user
            // yet — treat as anonymous rather than 500ing. JIT provisioning is
            // someone else's job; the search service is read-only.
            log.debug("No Canopy user for JWT sub {}; treating search as anonymous", uuid);
            return SearchAccessContext.anonymous();
        }
        AuthUser u = user.get();
        List<String> roleNames = u.getRoles() == null
                ? List.of()
                : u.getRoles().stream().map(AuthRole::getName).toList();
        return new SearchAccessContext(u.getId(), roleNames);
    }
}
