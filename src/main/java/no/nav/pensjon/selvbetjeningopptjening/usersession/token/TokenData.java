package no.nav.pensjon.selvbetjeningopptjening.usersession.token;

public class TokenData {

    private final boolean hasIdToken;
    private final String accessToken;
    private final String idToken;
    private final String refreshToken;

    public TokenData(String accessToken, String idToken, String refreshToken) {
        this.accessToken = accessToken;
        this.idToken = idToken;
        this.hasIdToken = idToken != null;
        this.refreshToken = refreshToken;
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
}
