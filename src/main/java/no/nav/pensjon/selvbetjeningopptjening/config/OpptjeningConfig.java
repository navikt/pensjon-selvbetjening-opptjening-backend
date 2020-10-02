package no.nav.pensjon.selvbetjeningopptjening.config;

import java.time.LocalDate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import no.nav.pensjon.selvbetjeningopptjening.consumer.pdl.PdlConsumer;
import no.nav.pensjon.selvbetjeningopptjening.util.FnrExtractor;
import no.nav.pensjon.selvbetjeningopptjening.util.SimpleStringExtractor;
import no.nav.security.token.support.core.context.TokenValidationContextHolder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import no.nav.pensjon.selvbetjeningopptjening.auth.serviceusertoken.OidcAuthTokenInterceptor;
import no.nav.pensjon.selvbetjeningopptjening.auth.serviceusertoken.ServiceUserTokenGetter;
import no.nav.pensjon.selvbetjeningopptjening.consumer.opptjeningsgrunnlag.OpptjeningsgrunnlagConsumer;
import no.nav.pensjon.selvbetjeningopptjening.consumer.pensjonsbeholdning.PensjonsbeholdningConsumer;
import no.nav.pensjon.selvbetjeningopptjening.consumer.pensjonspoeng.PensjonspoengConsumer;
import no.nav.pensjon.selvbetjeningopptjening.consumer.person.PersonConsumer;
import no.nav.pensjon.selvbetjeningopptjening.consumer.restpensjon.RestpensjonConsumer;
import no.nav.pensjon.selvbetjeningopptjening.consumer.uttaksgrad.UttaksgradConsumer;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.EndringPensjonsbeholdningCalculator;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.MerknadHandler;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.OpptjeningProvider;
import no.nav.pensjon.selvbetjeningopptjening.util.LocalDateTimeFromEpochDeserializer;

@Configuration
public class OpptjeningConfig {

    @Bean
    @Qualifier("conf.opptjening.resttemplate")
    public RestTemplate basicRestTemplate() {
        return new RestTemplate();
    }

    @Bean
    @Qualifier("conf.opptjening.resttemplate.oidc")
    public RestTemplate oidcRestTemplate(ServiceUserTokenGetter tokenGetter) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(0, createCustomMessageConverterForLocalDate());
        restTemplate.setInterceptors(Stream.of(new OidcAuthTokenInterceptor(tokenGetter)).collect(Collectors.toList()));
        return restTemplate;
    }

    @Bean
    public OpptjeningProvider opptjeningProvider() {
        return new OpptjeningProvider();
    }

    @Bean
    public PensjonspoengConsumer pensjonspoengConsumer(@Value("${popp.endpoint.url}") String endpoint) {
        return new PensjonspoengConsumer(endpoint);
    }

    @Bean
    public RestpensjonConsumer restpensjonConsumer(@Value("${popp.endpoint.url}") String endpoint) {
        return new RestpensjonConsumer(endpoint);
    }

    @Bean
    public PensjonsbeholdningConsumer pensjonsbeholdningConsumer(@Value("${popp.endpoint.url}") String endpoint) {
        return new PensjonsbeholdningConsumer(endpoint);
    }

    @Bean
    public OpptjeningsgrunnlagConsumer opptjeningsgrunnlagConsumer(@Value("${popp.endpoint.url}") String endpoint) {
        return new OpptjeningsgrunnlagConsumer(endpoint);
    }

    @Bean
    public UttaksgradConsumer uttaksgradConsumer(@Value("${pen.endpoint.url}") String endpoint) {
        return new UttaksgradConsumer(endpoint);
    }

    @Bean
    public PersonConsumer personConsumer(@Value("${pen.endpoint.url}") String endpoint) {
        return new PersonConsumer(endpoint);
    }

    @Bean
    public PdlConsumer pdlConsumer(@Value("${pdl.endpoint.url}") String endpoint,
                                   TokenValidationContextHolder context,
                                   ServiceUserTokenGetter tokenGetter) {
        return new PdlConsumer(endpoint, context, tokenGetter);
    }

    @Bean
    public EndringPensjonsbeholdningCalculator endringPensjonsbeholdningCalculator() {
        return new EndringPensjonsbeholdningCalculator();
    }

    @Bean
    public MerknadHandler merknadHandler() {
        return new MerknadHandler();
    }

    @Bean
    @Profile("default")
    public StringExtractor fnrExtractor(TokenValidationContextHolder context) {
        return new FnrExtractor(context);
    }

    @Bean
    @Profile("!default")
    public StringExtractor simpleFnrExtractor(@Value("${fnr}") String fnr) {
        return new SimpleStringExtractor(fnr);
    }

    private static MappingJackson2HttpMessageConverter createCustomMessageConverterForLocalDate() {
        var converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(objectMapper());
        return converter;
    }

    private static ObjectMapper objectMapper() {
        var objectMapper = new ObjectMapper();
        var module = new SimpleModule();
        module.addDeserializer(LocalDate.class, new LocalDateTimeFromEpochDeserializer());
        objectMapper.registerModule(module);
        return objectMapper;
    }
}
