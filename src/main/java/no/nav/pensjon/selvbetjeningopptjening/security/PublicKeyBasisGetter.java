package no.nav.pensjon.selvbetjeningopptjening.security;

import no.nav.pensjon.selvbetjeningopptjening.security.oauth2.key.PublicKeyBasis;

import java.security.KeyException;

/**
 * Getter of data used to build a public key.
 */
public interface PublicKeyBasisGetter {

    PublicKeyBasis getPublicKeyBasis(String id) throws KeyException;

    void refresh();
}
