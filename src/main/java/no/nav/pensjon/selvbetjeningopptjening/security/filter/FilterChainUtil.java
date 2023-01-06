package no.nav.pensjon.selvbetjeningopptjening.security.filter;

import javax.servlet.ServletRequest;

public class FilterChainUtil {

    private static final String FILTER_CHAIN_DATA_ATTRIBUTE_NAME = "FilterChainData";
    private static final String ACT_ON_BEHALF_URI = "/api/byttbruker";

    public static FilterChainData getAttribute(ServletRequest request) {
        Object data = request.getAttribute(FILTER_CHAIN_DATA_ATTRIBUTE_NAME);
        return data == null ? FilterChainData.defaultInstance() : (FilterChainData) data;
    }

    public static void setAttribute(ServletRequest request, Object value) {
        request.setAttribute(FILTER_CHAIN_DATA_ATTRIBUTE_NAME, value);
    }

    public static boolean isActOnBehalfRequest(String uri) {
        return ACT_ON_BEHALF_URI.equals(uri) || (ACT_ON_BEHALF_URI + "/").equals(uri);
    }
}
