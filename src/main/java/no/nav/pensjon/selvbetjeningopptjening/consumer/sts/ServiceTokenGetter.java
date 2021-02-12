package no.nav.pensjon.selvbetjeningopptjening.consumer.sts;

import no.nav.pensjon.selvbetjeningopptjening.security.token.ServiceTokenData;
import no.nav.pensjon.selvbetjeningopptjening.security.token.StsException;

public interface ServiceTokenGetter {

    ServiceTokenData getServiceUserToken() throws StsException;
}
