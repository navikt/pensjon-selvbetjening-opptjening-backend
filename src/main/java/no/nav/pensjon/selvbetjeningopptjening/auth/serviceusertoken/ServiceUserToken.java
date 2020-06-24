package no.nav.pensjon.selvbetjeningopptjening.auth.serviceusertoken;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public class ServiceUserToken {
    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("expires_in")
    private Long expiresIn;

    @JsonProperty("token_type")
    private String tokenType;

    private final LocalDateTime issuedTime = LocalDateTime.now();

    @SuppressWarnings("unused")
    public ServiceUserToken() {
        //Required by Jackson when mapping json object
    }

    public ServiceUserToken(String accessToken, Long expiresIn, String tokenType) {
        this.accessToken = accessToken;
        this.expiresIn = expiresIn;
        this.tokenType = tokenType;
    }

    /**
     * @param expirationLeeway the amount of seconds to be subtracted from the expirationTime to avoid returning false positives
     * @return <code>true</code> if "now" is after the expirationtime(minus leeway), else returns <code>false</code>
     */
    public boolean isExpired(long expirationLeeway) {
        return LocalDateTime.now().isAfter(issuedTime.plusSeconds(expiresIn).minusSeconds(expirationLeeway));
    }

    public String getAccessToken() {
        return accessToken;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public String getTokenType() {
        return tokenType;
    }

    @Override
    public String toString() {
        return "ServiceUserToken{" +
                "accessToken='" + accessToken + '\'' +
                ", expiresIn=" + expiresIn +
                ", tokenType='" + tokenType + '\'' +
                '}';
    }
}
