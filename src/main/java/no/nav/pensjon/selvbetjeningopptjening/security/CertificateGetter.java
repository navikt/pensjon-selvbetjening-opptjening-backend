package no.nav.pensjon.selvbetjeningopptjening.security;

import java.security.cert.CertificateException;

public interface CertificateGetter {

    String getCertificate(String id) throws CertificateException;

    void refresh();
}
