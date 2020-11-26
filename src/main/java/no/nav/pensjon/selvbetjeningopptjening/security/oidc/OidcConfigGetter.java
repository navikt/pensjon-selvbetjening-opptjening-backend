package no.nav.pensjon.selvbetjeningopptjening.security.oidc;

public interface OidcConfigGetter {

    String getIssuer();

    String getAuthorizationEndpoint();

    String getTokenEndpoint();

    String getJsonWebKeySetUri();

    void refresh();
}
