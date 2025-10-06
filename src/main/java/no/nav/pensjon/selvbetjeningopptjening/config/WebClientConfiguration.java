package no.nav.pensjon.selvbetjeningopptjening.config;

import io.netty.handler.logging.LogLevel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.logging.AdvancedByteBufFormat;

@Configuration
public class WebClientConfiguration {

    @Bean
    @Primary
    WebClient webClient() {
        var httpClient = HttpClient.create()
                .wiretap("reactor.netty.http.client.HttpClient",
                        LogLevel.DEBUG,
                        AdvancedByteBufFormat.TEXTUAL);

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }

    @Bean
    @Qualifier("epoch-support")
    WebClient webClientWithEpochSupport() {
        var httpClient = HttpClient.create().wiretap(
                "reactor.netty.http.client.HttpClient",
                LogLevel.DEBUG,
                AdvancedByteBufFormat.TEXTUAL);

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .exchangeStrategies(JsonEpochExchangeStrategies.build())
                .build();
    }
}
