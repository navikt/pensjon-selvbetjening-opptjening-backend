package no.nav.pensjon.selvbetjeningopptjening.security.token;

import java.time.LocalDateTime;

public class ServiceTokenData {

    private final String accessToken;
    private final String tokenType;
    private final LocalDateTime issuedTime;
    private final Long expiresInSeconds;

    public ServiceTokenData(String accessToken, String tokenType, LocalDateTime issuedTime, Long expiresInSeconds) {
        this.accessToken = accessToken;
        this.tokenType = tokenType;
        this.issuedTime = issuedTime;
        this.expiresInSeconds = expiresInSeconds;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public Long getExpiresInSeconds() {
        return expiresInSeconds;
    }

    public String getTokenType() {
        return tokenType;
    }

    @Override
    public String toString() {
        return "ServiceUserToken{" +
                "accessToken='" + accessToken + '\'' +
                ", expiresInSeconds=" + expiresInSeconds +
                ", tokenType='" + tokenType + '\'' +
                '}';
    }

    public LocalDateTime getIssuedTime() {
        return issuedTime;
    }
}
