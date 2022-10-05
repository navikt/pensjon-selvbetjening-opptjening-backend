package no.nav.pensjon.selvbetjeningopptjening.security.impersonal;

public interface Oauth2ConfigGetter {

    String getIssuer();

    String getAuthorizationEndpoint();

    String getTokenEndpoint();

    String getEndSessionEndpoint();

    String getJsonWebKeySetUri();

    void refresh();
}
