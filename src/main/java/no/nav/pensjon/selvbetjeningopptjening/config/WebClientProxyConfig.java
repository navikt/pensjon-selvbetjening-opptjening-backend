package no.nav.pensjon.selvbetjeningopptjening.config;

import no.nav.pensjon.selvbetjeningopptjening.util.net.UriUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.ProxyProvider;

import java.net.URI;
import java.net.URISyntaxException;

public class WebClientProxyConfig {

    private static final Logger log = LoggerFactory.getLogger(WebClientProxyConfig.class);

    public static ReactorClientHttpConnector clientHttpConnector(String proxyUri) throws URISyntaxException {
        URI uri = UriUtil.uriFrom(proxyUri);
        log.info("URI: Host: '{}'. Port: {}.", uri.getHost(), uri.getPort());

        var httpClient = HttpClient
                .create()
                .proxy(p -> proxySpec(p, uri.getHost(), uri.getPort()));

        return new ReactorClientHttpConnector(httpClient);
    }

    @SuppressWarnings("UnusedReturnValue")
    private static ProxyProvider.Builder proxySpec(ProxyProvider.TypeSpec proxy, String host, int port) {
        return proxy
                .type(ProxyProvider.Proxy.HTTP)
                .host(host)
                .port(port);
    }
}
