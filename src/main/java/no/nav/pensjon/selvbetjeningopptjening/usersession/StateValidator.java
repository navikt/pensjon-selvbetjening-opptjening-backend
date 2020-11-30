package no.nav.pensjon.selvbetjeningopptjening.usersession;

import no.nav.pensjon.selvbetjeningopptjening.security.crypto.Crypto;
import no.nav.pensjon.selvbetjeningopptjening.security.crypto.CryptoException;
import no.nav.pensjon.selvbetjeningopptjening.security.oauth2.Oauth2FlowException;

import static java.lang.System.currentTimeMillis;
import static org.springframework.util.StringUtils.hasText;

class StateValidator {

    private static final String DELIMITER = ":";
    private static final String URI_START = "/api";
    private static final long MAX_DELAY_MILLIS = 3600000L;
    private final Crypto crypto;

    StateValidator(Crypto crypto) {
        this.crypto = crypto;
    }

    String extractRedirectUri(String encryptedState) throws CryptoException, Oauth2FlowException {
        if (!hasText(encryptedState)) {
            throw new Oauth2FlowException("Missing state");
        }

        String state = crypto.decrypt(encryptedState);
        String[] split = state.split(DELIMITER);

        if (split.length > 2) {
            throw new Oauth2FlowException("Invalid state format; probably unencoded redirect URI");
        }

        if (split.length != 2) {
             throw new Oauth2FlowException("Invalid state format");
        }

        long stateMillis = Long.parseLong(split[0]);
        long currentMillis = getCurrentMillis();

        if (currentMillis < stateMillis || stateMillis < currentMillis - MAX_DELAY_MILLIS) {
            throw new Oauth2FlowException("Invalid state (1)");
        }

        String redirectUri = split[1];

        if (!redirectUri.startsWith(URI_START)) {
            throw new Oauth2FlowException("Invalid state (2)");
        }

        return redirectUri;
    }

    protected long getCurrentMillis() {
        return currentTimeMillis();
    }
}
