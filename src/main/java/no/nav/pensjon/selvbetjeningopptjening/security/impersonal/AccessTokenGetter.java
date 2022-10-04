package no.nav.pensjon.selvbetjeningopptjening.security.impersonal;

import no.nav.pensjon.selvbetjeningopptjening.security.token.RawJwt;

public interface AccessTokenGetter {

    RawJwt getAccessToken(String audience);

    void clearAccessToken(String audience);
}
