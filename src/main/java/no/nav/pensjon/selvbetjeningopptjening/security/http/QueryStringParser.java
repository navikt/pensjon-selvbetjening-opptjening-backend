package no.nav.pensjon.selvbetjeningopptjening.security.http;

import java.util.Arrays;
import java.util.Map;

import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.toMap;
import static org.springframework.util.StringUtils.hasText;

public class QueryStringParser {

    private static Map<String, String> asMap(String queryString) {
        if (!hasText(queryString)) {
            return emptyMap();
        }

        return Arrays.stream(queryString.split("&"))
                .map(kvString -> kvString.split("="))
                .filter(kv -> kv.length == 2)
                .collect(toMap(kv -> kv[0], kv -> kv[1]));
    }

    public static String getValue(String queryString, String queryParamName) {
        if (!hasText(queryString) || !hasText(queryParamName)) {
            return "";
        }

        String value = asMap(queryString).get(queryParamName);
        return value == null ? "" : value;
    }
}
