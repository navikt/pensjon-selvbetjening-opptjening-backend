package no.nav.pensjon.selvbetjeningopptjening.security.token;

public interface AccessTokenGetter {

    RawJwt getAccessToken(String ingressToken, String audience, String pid);

    void clearAccessToken(String audience, String pid);
}
