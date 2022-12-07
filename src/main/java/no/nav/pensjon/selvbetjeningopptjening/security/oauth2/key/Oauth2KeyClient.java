package no.nav.pensjon.selvbetjeningopptjening.security.oauth2.key;

import no.nav.pensjon.selvbetjeningopptjening.security.PublicKeyBasisGetter;
import no.nav.pensjon.selvbetjeningopptjening.security.dto.JwtKeyDto;
import no.nav.pensjon.selvbetjeningopptjening.security.dto.JwtKeysDto;
import no.nav.pensjon.selvbetjeningopptjening.security.impersonal.Oauth2ConfigGetter;
import org.springframework.web.reactive.function.client.WebClient;

import java.security.KeyException;
import java.util.List;

import static java.util.Objects.requireNonNull;

public class Oauth2KeyClient implements PublicKeyBasisGetter {

    private final WebClient webClient;
    private final Oauth2ConfigGetter oauth2ConfigGetter;
    private JwtKeysDto keys;

    public Oauth2KeyClient(WebClient webClient, Oauth2ConfigGetter oauth2ConfigGetter) {
        this.webClient = requireNonNull(webClient);
        this.oauth2ConfigGetter = requireNonNull(oauth2ConfigGetter);
    }

    @Override
    public PublicKeyBasis getPublicKeyBasis(String keyId) throws KeyException {
        return getCachedKeys().getKeys()
                .stream()
                .filter(key -> match(key, keyId))
                .findFirst()
                .map(this::getPublicKeyBasis)
                .orElseThrow(() -> noKeyBasisFound(keyId));
    }

    private PublicKeyBasis getPublicKeyBasis(JwtKeyDto key) {
        return hasEntry(key.getX5c())
                ? PublicKeyBasis.certificateBased(key.getKid(), key.getUse(), key.getX5c().get(0))
                : PublicKeyBasis.modulusBased(key.getKid(), key.getUse(), key.getE(), key.getN());
    }

    private static boolean hasEntry(List<String> list) {
        return list != null && list.size() > 0;
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
        oauth2ConfigGetter.refresh();
    }

    private JwtKeysDto getFreshKeys() {
        return webClient
                .get()
                .uri(oauth2ConfigGetter.getJsonWebKeySetUri())
                .retrieve()
                .bodyToMono(JwtKeysDto.class)
                .block();
    }

    private JwtKeysDto getCachedKeys() {
        return keys == null
                ? (keys = getFreshKeys())
                : keys;
    }

    private static KeyException noKeyBasisFound(String keyId) {
        return new KeyException(String.format("No public key basis found for key ID '%s'", keyId));
    }
}
