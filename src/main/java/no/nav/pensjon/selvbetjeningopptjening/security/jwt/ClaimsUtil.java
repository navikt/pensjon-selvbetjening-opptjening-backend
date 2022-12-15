package no.nav.pensjon.selvbetjeningopptjening.security.jwt;

import io.jsonwebtoken.Claims;

import java.util.List;

import static java.util.Collections.emptyList;

/**
 * Utilities related to JSON web token claims.
 * https://auth0.com/docs/secure/tokens/json-web-tokens/json-web-token-claims
 */
public class ClaimsUtil {

    private static final String GROUPS_CLAIM_KEY = "groups";
    private static final String INTERNAL_USER_ID_CLAIM_KEY = "NAVident";

    public static List<String> getGroups(Claims claims) {
        var groupIds = (List<?>) claims.get(GROUPS_CLAIM_KEY);

        return groupIds == null ? emptyList()
                :
                groupIds.stream()
                        .map(Object::toString)
                        .toList();
    }

    public static String getInternalUserId(Claims claims) {
        Object claim = claims.get(INTERNAL_USER_ID_CLAIM_KEY);
        return claim == null ? "" : (String) claim;
    }
}
