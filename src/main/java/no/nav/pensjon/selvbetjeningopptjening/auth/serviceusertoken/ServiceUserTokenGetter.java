package no.nav.pensjon.selvbetjeningopptjening.auth.serviceusertoken;

public interface ServiceUserTokenGetter {

    ServiceUserToken getServiceUserToken() throws StsException;
}
