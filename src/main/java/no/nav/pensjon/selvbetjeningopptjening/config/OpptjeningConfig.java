package no.nav.pensjon.selvbetjeningopptjening.config;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import no.nav.pensjon.selvbetjeningopptjening.auth.serviceusertoken.OidcAuthTokenInterceptor;
import no.nav.pensjon.selvbetjeningopptjening.consumer.pensjonspoeng.PensjonspoengConsumer;
import no.nav.pensjon.selvbetjeningopptjening.consumer.restpensjon.RestpensjonConsumer;
import no.nav.pensjon.selvbetjeningopptjening.auth.serviceusertoken.ServiceUserTokenGetter;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.OpptjeningProvider;

@Configuration
public class OpptjeningConfig {

    @Bean
    @Qualifier("conf.opptjening.resttemplate")
    public RestTemplate basicRestTemplate() {
        return new RestTemplate();
    }

    @Bean
    @Qualifier("conf.opptjening.resttemplate.oidc")
    public RestTemplate oidcRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setInterceptors(Stream.of(new OidcAuthTokenInterceptor(serviceUserTokenGetter())).collect(Collectors.toList()));
        return restTemplate;
    }

    @Bean
    public OpptjeningProvider opptjeningProvider() {
        return new OpptjeningProvider();
    }

    @Bean
    public ServiceUserTokenGetter serviceUserTokenGetter() {
        return new ServiceUserTokenGetter();
    }

    @Bean
    public PensjonspoengConsumer pensjonspoengConsumer(@Value("${pensjonspoeng.endpoint.url}") String endpoint) {
        return new PensjonspoengConsumer(endpoint);
    }

    @Bean
    public RestpensjonConsumer restpensjonConsumer(@Value("${restpensjon.endpoint.url}") String endpoint) {
        return new RestpensjonConsumer(endpoint);
    }
}
