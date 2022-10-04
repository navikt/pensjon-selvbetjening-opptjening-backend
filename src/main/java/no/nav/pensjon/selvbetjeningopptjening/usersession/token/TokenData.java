package no.nav.pensjon.selvbetjeningopptjening.usersession.token;

import java.time.LocalDateTime;

import static java.util.Objects.requireNonNull;

public class TokenData {

    private final boolean hasIdToken;
    private final boolean hasRefreshToken;
    private final String accessToken;
    private final String idToken;
    private final String refreshToken;
    private final LocalDateTime issuedTime;
    private final long expiresInSeconds;

    public TokenData(String accessToken,
                     String idToken,
                     String refreshToken,
                     LocalDateTime issuedTime,
                     Long expiresInSeconds) {
        this.accessToken = requireNonNull(accessToken, "accessToken");
        this.idToken = idToken == null ? "" : idToken;
        this.hasIdToken = idToken != null;
        this.refreshToken = refreshToken == null ? "" : refreshToken;
        this.hasRefreshToken = refreshToken != null;
        this.issuedTime = requireNonNull(issuedTime, "issuedTime");
        this.expiresInSeconds = requireNonNull(expiresInSeconds, "expiresInSeconds");
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getIdToken() {
        return idToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public boolean hasIdToken() {
        return hasIdToken;
    }

    public boolean hasRefreshToken() {
        return hasRefreshToken;
    }

    public LocalDateTime getIssuedTime() {
        return issuedTime;
    }

    public long getExpiresInSeconds() {
        return expiresInSeconds;
    }
}
