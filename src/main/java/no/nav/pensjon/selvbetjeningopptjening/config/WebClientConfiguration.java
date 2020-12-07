package no.nav.pensjon.selvbetjeningopptjening.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.ProxyProvider;
import reactor.netty.tcp.TcpClient;

import java.net.URI;
import java.net.URISyntaxException;

@Configuration
public class WebClientConfiguration {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Bean
    @Primary
    WebClient webClient() {
        var httpClient = HttpClient.create()
                .wiretap(true);

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }

    @Bean
    @Qualifier("external-call")
    WebClient webClientForExternalCalls(
            @Value("${http.proxy.parametername}") String proxyParameterName,
            @Value("${http.proxy.uri}") String proxyUri) {
        log.info("WebClient proxy: Parameter: '{}'. URI: '{}'.", proxyParameterName, proxyUri);

        if ("notinuse".equalsIgnoreCase(proxyParameterName)) {
            return WebClient.create();
        }

        try {
            return proxiedWebClient(proxyUri);
        } catch (URISyntaxException e) {
            log.warn("Proxy not used. Reason: Bad URI: '{}'. Message: '{}'.", proxyUri, e.getMessage());
            return WebClient.create();
        }
    }

    private WebClient proxiedWebClient(String proxyUri) throws URISyntaxException {
        URI uri = getValidUri(proxyUri);

        var httpClient = HttpClient.create()
                .tcpConfiguration(c -> tcpClient(c, uri.getHost(), uri.getPort()));

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }

    private URI getValidUri(String value) throws URISyntaxException {
        var uri = new URI(value);
        log.info("URI: Host: '{}'. Port: {}.", uri.getHost(), uri.getPort());

        if (uri.getPort() < 0) {
            throw new URISyntaxException(value, "No URI port specified");
        }

        return uri;
    }

    private TcpClient tcpClient(TcpClient client, String proxyHost, int proxyPort) {
        return client.proxy(p -> proxySpec(p, proxyHost, proxyPort));
    }

    @SuppressWarnings("UnusedReturnValue")
    private ProxyProvider.Builder proxySpec(ProxyProvider.TypeSpec proxy, String host, int port) {
        return proxy
                .type(ProxyProvider.Proxy.HTTP)
                .host(host)
                .port(port);
    }
}
