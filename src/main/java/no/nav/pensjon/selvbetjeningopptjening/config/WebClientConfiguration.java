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
            return webClientWithProxySupport(proxyUri);
        } catch (URISyntaxException e) {
            log.warn("Proxy not used. Reason: Bad URI: '{}'. Message: '{}'.", proxyUri, e.getMessage());
            return WebClient.create();
        }
    }

    @Bean
    @Qualifier("epoch-support")
    WebClient webClientWithEpochSupport() {
        return WebClient.builder()
                .exchangeStrategies(JsonEpochExchangeStrategies.build())
                .build();
    }

    private WebClient webClientWithProxySupport(String proxyUri) throws URISyntaxException {
        return WebClient.builder()
                .clientConnector(WebClientProxyConfig.getClientHttpConnector(proxyUri))
                .build();
    }
}
