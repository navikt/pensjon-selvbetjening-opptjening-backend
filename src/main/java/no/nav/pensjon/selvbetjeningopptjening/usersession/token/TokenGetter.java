package no.nav.pensjon.selvbetjeningopptjening.usersession.token;

public interface TokenGetter {

    TokenData getTokenData(TokenAccessParam accessParam, String audience);
}
