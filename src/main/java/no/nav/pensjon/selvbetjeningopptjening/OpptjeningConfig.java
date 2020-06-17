package no.nav.pensjon.selvbetjeningopptjening;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import no.nav.pensjon.selvbetjeningopptjening.consumers.HentSystembrukerToken;
import no.nav.pensjon.selvbetjeningopptjening.consumers.PensjonspoengConsumer;

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
        restTemplate.setInterceptors(Stream.of(new OidcAuthTokenInterceptor(hentSystembrukerToken())).collect(Collectors.toList()));
        return restTemplate;
    }

    @Bean
    public HentSystembrukerToken hentSystembrukerToken() {
        return new HentSystembrukerToken();
    }

    @Bean
    public PensjonspoengConsumer pensjonspoengConsumer(@Value("${pensjonspoeng.endpoint.url}") String endpoint) {
        return new PensjonspoengConsumer(endpoint);
    }
}
