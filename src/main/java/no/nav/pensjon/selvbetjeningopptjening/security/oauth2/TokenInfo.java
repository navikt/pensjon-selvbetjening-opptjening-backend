package no.nav.pensjon.selvbetjeningopptjening.security.oauth2;

import io.jsonwebtoken.Claims;
import no.nav.pensjon.selvbetjeningopptjening.security.UserType;

import static org.springframework.util.StringUtils.hasText;

public class TokenInfo {

    private final String jwt;
    private final boolean valid;
    private final boolean hasClaims;
    private final boolean hasUser;
    private final UserType userType;
    private final Claims claims;
    private final String userId;

    public String getJwt() {
        return jwt;
    }

    public boolean isValid() {
        return valid;
    }

    public boolean hasClaims() {
        return hasClaims;
    }

    public boolean hasUser() {
        return hasUser;
    }

    public UserType getUserType() {
        return userType;
    }

    public Claims getClaims() {
        return claims;
    }

    public String getUserId() {
        return userId;
    }

    public static TokenInfo valid(String jwt, UserType userType, Claims claims, String userId) {
        return new TokenInfo(jwt, true, userType, claims, userId);
    }

    public static TokenInfo forSelfTest() {
        return new TokenInfo("", true, UserType.SELF_TEST, null, "");
    }

    public static TokenInfo invalid(String jwt, UserType userType, Claims claims, String userId) {
        return new TokenInfo(jwt, false, userType, claims, userId);
    }

    public static TokenInfo invalid(String jwt) {
        return invalid(jwt, UserType.NONE, null, "");
    }

    public static TokenInfo invalid() {
        return invalid("");
    }

    private TokenInfo(String jwt, boolean valid, UserType userType, Claims claims, String userId) {
        this.jwt = jwt == null ? "" : jwt;
        this.valid = valid;
        this.userType = userType == null ? UserType.NONE : userType;
        this.claims = claims;
        this.hasClaims = claims != null;
        this.userId = userId == null ? "" : userId;
        this.hasUser = hasText(userId);
    }
}
