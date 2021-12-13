package no.nav.pensjon.selvbetjeningopptjening.util.net;

import java.net.URI;
import java.net.URISyntaxException;

public class UriUtil {

    public static URI uriFrom(String value) throws URISyntaxException {
        var uri = new URI(value);

        if (uri.getPort() < 0) {
            throw new URISyntaxException(value, "No URI port specified");
        }

        return uri;
    }

    private UriUtil() {
    }
}
