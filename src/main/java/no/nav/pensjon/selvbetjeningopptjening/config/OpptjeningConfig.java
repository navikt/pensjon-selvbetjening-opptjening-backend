package no.nav.pensjon.selvbetjeningopptjening.config;

import java.time.LocalDate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
    public RestTemplate oidcRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(0, createCustomMessageConverterForLocalDate());
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
    public PensjonspoengConsumer pensjonspoengConsumer(@Value("${popp.endpoint.url}") String endpoint) {
        return new PensjonspoengConsumer(endpoint);
    }

    @Bean
    public RestpensjonConsumer restpensjonConsumer(@Value("${popp.endpoint.url}") String endpoint) {
        return new RestpensjonConsumer(endpoint);
    }

    @Bean
    public PensjonsbeholdningConsumer pensjonsbeholdningConsumer(@Value("${popp.endpoint.url}") String endpoint){
        return new PensjonsbeholdningConsumer(endpoint);
    }

    @Bean
    public OpptjeningsgrunnlagConsumer opptjeningsgrunnlagConsumer(@Value("${popp.endpoint.url}") String endpoint){
        return new OpptjeningsgrunnlagConsumer(endpoint);
    }

    @Bean
    public UttaksgradConsumer uttaksgradConsumer(@Value("${pen.endpoint.url}") String endpoint){
        return new UttaksgradConsumer(endpoint);
    }

    @Bean
    public PersonConsumer personConsumer(@Value("${pen.endpoint.url}") String endpoint){
        return new PersonConsumer(endpoint);
    }

    @Bean
    public EndringPensjonsbeholdningCalculator endringPensjonsbeholdningCalculator(){
        return new EndringPensjonsbeholdningCalculator();
    }

    @Bean
    public MerknadHandler merknadHandler(){
        return new MerknadHandler();
    }

    private MappingJackson2HttpMessageConverter createCustomMessageConverterForLocalDate(){
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(LocalDate.class, new LocalDateTimeFromEpochDeserializer());
        objectMapper.registerModule(module);

        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(objectMapper);
        return converter;
    }
}
