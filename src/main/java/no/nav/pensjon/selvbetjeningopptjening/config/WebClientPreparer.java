package no.nav.pensjon.selvbetjeningopptjening.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URISyntaxException;

public class WebClientPreparer {

    private static final Logger log = LoggerFactory.getLogger(WebClientPreparer.class);

    public static WebClient webClient(boolean requiresProxy, String proxyUri) {
        if (!requiresProxy) {
            return WebClient.create();
        }

        try {
            return proxyAwareWebClient(proxyUri);
        } catch (URISyntaxException e) {
            log.warn("Proxy not used. Reason: Bad URI: '{}'. Message: '{}'.", proxyUri, e.getMessage());
            return WebClient.create();
        }
    }

    public static WebClient proxyAwareWebClient(String proxyUri) throws URISyntaxException {
        return WebClient.builder()
                .clientConnector(WebClientProxyConfig.clientHttpConnector(proxyUri))
                .build();
    }

    private WebClientPreparer() {
    }
}
