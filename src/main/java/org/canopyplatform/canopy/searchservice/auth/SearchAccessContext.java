package org.canopyplatform.canopy.searchservice.auth;

import java.util.List;
import java.util.Set;

/**
 * What the search service needs to know about the caller in order to build
 * the access-level filter for the studies index. Created by
 * {@link SearchAccessContextResolver} from the inbound JWT (or null for
 * anonymous callers).
 *
 * <ul>
 *   <li>Anonymous → only PUBLIC studies are visible.</li>
 *   <li>Authenticated, non-override → PUBLIC + LIMITED studies, plus PRIVATE
 *       studies the user created.</li>
 *   <li>Authenticated, has Curator or Admin role → no filter.</li>
 * </ul>
 */
public record SearchAccessContext(
        Integer userId,
        List<String> roleNames
) {
    private static final Set<String> OVERRIDE_ROLES = Set.of("Data Curator", "Application Administrator");

    public static SearchAccessContext anonymous() {
        return new SearchAccessContext(null, List.of());
    }

    public boolean isAnonymous() {
        return userId == null;
    }

    public boolean hasOverrideRole() {
        return roleNames.stream().anyMatch(OVERRIDE_ROLES::contains);
    }
}
