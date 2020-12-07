package no.nav.pensjon.selvbetjeningopptjening.usersession;

import no.nav.pensjon.selvbetjeningopptjening.security.CertificateGetter;
import no.nav.pensjon.selvbetjeningopptjening.security.dto.JwtKeyDto;
import no.nav.pensjon.selvbetjeningopptjening.security.dto.JwtKeysDto;
import no.nav.pensjon.selvbetjeningopptjening.security.oidc.OidcConfigGetter;
import org.springframework.web.reactive.function.client.WebClient;

import java.security.cert.CertificateException;

import static java.util.Objects.requireNonNull;

public abstract class OidcCertificateGetter implements CertificateGetter {

    private final WebClient webClient;
    private final OidcConfigGetter oidcConfigGetter;
    private JwtKeysDto keys;

    protected OidcCertificateGetter(WebClient webClient, OidcConfigGetter oidcConfigGetter) {
        this.webClient = requireNonNull(webClient);
        this.oidcConfigGetter = requireNonNull(oidcConfigGetter);
    }

    @Override
    public String getCertificate(String keyId) throws CertificateException {
        return getCachedKeys().getKeys()
                .stream()
                .filter(key -> match(key, keyId))
                .findFirst()
                .map(key -> key.getX5c().get(0))
                .orElseThrow(() -> noCertificateFound(keyId));
    }

    private static boolean match(JwtKeyDto key, String keyId) {
        if (keyId == null) {
            throw new IllegalArgumentException("Illegal keyId value: null");
        }

        return keyId.equals(key.getKid());
    }

    @Override
    public void refresh() {
        keys = null;
        oidcConfigGetter.refresh();
    }

    private JwtKeysDto getFreshKeys() {
        return webClient
                .get()
                .uri(oidcConfigGetter.getJsonWebKeySetUri())
                .retrieve()
                .bodyToMono(JwtKeysDto.class)
                .block();
    }

    private JwtKeysDto getCachedKeys() {
        return keys == null
                ? (keys = getFreshKeys())
                : keys;
    }

    private static CertificateException noCertificateFound(String keyId) {
        return new CertificateException(String.format("No certificate found for key ID '%s'", keyId));
    }
}
