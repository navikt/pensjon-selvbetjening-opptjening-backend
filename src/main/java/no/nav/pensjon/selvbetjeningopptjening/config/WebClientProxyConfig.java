package no.nav.pensjon.selvbetjeningopptjening.config;

import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.ProxyProvider;
import reactor.netty.tcp.TcpClient;

import java.net.URI;
import java.net.URISyntaxException;

class WebClientProxyConfig {

    static ReactorClientHttpConnector getClientHttpConnector(String proxyUri) throws URISyntaxException {
        URI uri = getValidUri(proxyUri);

        var httpClient = HttpClient.create()
                .tcpConfiguration(c -> tcpClient(c, uri.getHost(), uri.getPort()));

        return new ReactorClientHttpConnector(httpClient);
    }

    private static URI getValidUri(String value) throws URISyntaxException {
        var uri = new URI(value);

        if (uri.getPort() < 0) {
            throw new URISyntaxException(value, "No URI port specified");
        }

        return uri;
    }

    private static TcpClient tcpClient(TcpClient client, String proxyHost, int proxyPort) {
        return client.proxy(p -> proxySpec(p, proxyHost, proxyPort));
    }

    @SuppressWarnings("UnusedReturnValue")
    private static ProxyProvider.Builder proxySpec(ProxyProvider.TypeSpec proxy, String host, int port) {
        return proxy
                .type(ProxyProvider.Proxy.HTTP)
                .host(host)
                .port(port);
    }
}
