package no.nav.pensjon.selvbetjeningopptjening.util.net;

import java.net.URI;
import java.net.URISyntaxException;

import static java.lang.String.format;
import static org.springframework.util.StringUtils.hasText;

public final class UriUtil {

    public static URI uriFrom(String value) throws URISyntaxException {
        var uri = new URI(value);

        if (uri.getPort() < 0) {
            throw new URISyntaxException(value, "No URI port specified");
        }

        return uri;
    }

    /**
     * Ref. URI Generic Syntax, https://datatracker.ietf.org/doc/html/rfc3986#section-3
     */
    public static String formatAsUri(String scheme, String authority, String path) {
        return hasText(path)
                ? format("%s://%s/%s", scheme, authority, path)
                : format("%s://%s", scheme, authority);
    }

    private UriUtil() {
    }
}
