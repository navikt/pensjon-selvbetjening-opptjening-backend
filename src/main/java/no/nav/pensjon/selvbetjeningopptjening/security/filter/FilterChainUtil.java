package no.nav.pensjon.selvbetjeningopptjening.security.filter;

import jakarta.servlet.ServletRequest;

public class FilterChainUtil {

    private static final String FILTER_CHAIN_DATA_ATTRIBUTE_NAME = "FilterChainData";

    public static FilterChainData getAttribute(ServletRequest request) {
        Object data = request.getAttribute(FILTER_CHAIN_DATA_ATTRIBUTE_NAME);
        return data == null ? FilterChainData.defaultInstance() : (FilterChainData) data;
    }

    public static void setAttribute(ServletRequest request, Object value) {
        request.setAttribute(FILTER_CHAIN_DATA_ATTRIBUTE_NAME, value);
    }
}
