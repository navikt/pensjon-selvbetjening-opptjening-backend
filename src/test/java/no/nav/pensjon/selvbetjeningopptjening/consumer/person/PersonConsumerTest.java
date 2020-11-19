package no.nav.pensjon.selvbetjeningopptjening.consumer.person;

import no.nav.pensjon.selvbetjeningopptjening.consumer.FailedCallingExternalServiceException;
import no.nav.pensjon.selvbetjeningopptjening.model.AfpHistorikkDto;
import no.nav.pensjon.selvbetjeningopptjening.model.UforeHistorikkDto;
import no.nav.pensjon.selvbetjeningopptjening.model.UforeperiodeDto;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.AfpHistorikk;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.UforeHistorikk;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Objects;

import static java.util.Collections.singletonList;
import static no.nav.pensjon.selvbetjeningopptjening.util.Constants.PEN;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PersonConsumerTest {

    private static final String ENDPOINT = "http://penEndpoint.test";
    private static final String AFP_HISTORIKK_URL = ENDPOINT + "/person/afphistorikk";
    private static final String UFOREHISTORIKK_URL = ENDPOINT + "/person/uforehistorikk";
    private static final String EXPECTED_AFP_HISTORIKK_IDENTIFIER = "PROPEN2602 getAfphistorikkForPerson";
    private static final String EXPECTED_UFOREHISTORIKK_IDENTIFIER = "PROPEN2603 getUforehistorikkForPerson";
    private static final LocalDate DATE = LocalDate.of(1991, 1, 1);
    private PersonConsumer consumer;

    @Mock
    private RestTemplate restTemplateMock;
    @Captor
    private ArgumentCaptor<String> urlCaptor;
    @Captor
    private ArgumentCaptor<HttpMethod> httpMethodCaptor;
    @Captor
    private ArgumentCaptor<HttpEntity<Object>> httpEntityCaptor;

    @BeforeEach
    void setup() {
        consumer = new PersonConsumer(ENDPOINT);
        consumer.setRestTemplate(restTemplateMock);
    }

    @Test
    void should_return_Afphistorikk_when_getAfpHistorikkForPerson() {
        var dto = new AfpHistorikkDto();
        dto.setVirkFom(DATE);
        ResponseEntity<AfpHistorikkDto> entity = new ResponseEntity<>(dto, HttpStatus.OK);
        when(restTemplateMock.exchange(eq(AFP_HISTORIKK_URL), any(), any(), eq(AfpHistorikkDto.class))).thenReturn(entity);

        AfpHistorikk historikk = consumer.getAfpHistorikkForPerson("");

        assertEquals(DATE, historikk.getVirkningFomDate());
    }

    @Test
    void should_add_fnr_as_headerparam_in_GET_when_getAfpHistorikkForPerson() {
        String expectedFnr = "expFnr";
        when(restTemplateMock.exchange(urlCaptor.capture(), httpMethodCaptor.capture(), httpEntityCaptor.capture(), eq(AfpHistorikkDto.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        consumer.getAfpHistorikkForPerson(expectedFnr);

        assertThat(httpMethodCaptor.getValue(), is(HttpMethod.GET));
        assertThat(urlCaptor.getValue(), is(AFP_HISTORIKK_URL));
        assertThat("Request should contain header pid", httpEntityCaptor.getValue().getHeaders().containsKey("pid"), is(true));
        assertThat(Objects.requireNonNull(httpEntityCaptor.getValue().getHeaders().get("pid")).get(0), is(expectedFnr));
    }

    @Test
    void should_get_Uforehistorikk_when_getUforeHistorikkForPerson() {
        var historikkDto = new UforeHistorikkDto();
        UforeperiodeDto uforeperiode = new UforeperiodeDto();
        uforeperiode.setUfgFom(DATE);
        historikkDto.setUforeperiodeListe(singletonList(uforeperiode));
        ResponseEntity<UforeHistorikkDto> entity = new ResponseEntity<>(historikkDto, HttpStatus.OK);
        when(restTemplateMock.exchange(eq(UFOREHISTORIKK_URL), any(), any(), eq(UforeHistorikkDto.class))).thenReturn(entity);

        UforeHistorikk historikk = consumer.getUforeHistorikkForPerson("");

        assertEquals(1, historikk.getUforeperioder().size());
        assertEquals(DATE, historikk.getUforeperioder().get(0).getFomDate());
    }

    @Test
    void should_add_fnr_as_headerparam_in_GET_when_getUforehistorikkForPerson() {
        String expectedFnr = "expFnr";
        when(restTemplateMock.exchange(urlCaptor.capture(), httpMethodCaptor.capture(), httpEntityCaptor.capture(), eq(UforeHistorikkDto.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        consumer.getUforeHistorikkForPerson(expectedFnr);

        assertThat(httpMethodCaptor.getValue(), is(HttpMethod.GET));
        assertThat(urlCaptor.getValue(), is(UFOREHISTORIKK_URL));
        assertThat("Request should contain header pid", httpEntityCaptor.getValue().getHeaders().containsKey("pid"), is(true));
        assertThat(Objects.requireNonNull(httpEntityCaptor.getValue().getHeaders().get("pid")).get(0), is(expectedFnr));
    }

    @Test
    void should_throw_FailedCallingExternalServiceException_when_401_from_getAfphistorikkForPerson() {
        when(restTemplateMock.exchange(eq(AFP_HISTORIKK_URL), any(), any(), eq(AfpHistorikkDto.class)))
                .thenThrow(new RestClientResponseException("", HttpStatus.UNAUTHORIZED.value(), "", null, null, null));

        FailedCallingExternalServiceException thrown = assertThrows(
                FailedCallingExternalServiceException.class,
                () -> consumer.getAfpHistorikkForPerson(""));

        assertThat(thrown.getMessage(), is("Error when calling the external service " + EXPECTED_AFP_HISTORIKK_IDENTIFIER + " in " + PEN + ". Received 401 UNAUTHORIZED"));
    }

    @Test
    void should_throw_FailedCallingExternalServiceException_when_500_from_getUttaksgradForVedtak() {
        when(restTemplateMock.exchange(eq(AFP_HISTORIKK_URL), any(), any(), eq(AfpHistorikkDto.class)))
                .thenThrow(new RestClientResponseException("", HttpStatus.INTERNAL_SERVER_ERROR.value(), "", null, null, null));

        FailedCallingExternalServiceException thrown = assertThrows(
                FailedCallingExternalServiceException.class,
                () -> consumer.getAfpHistorikkForPerson(""));

        assertThat(thrown.getMessage(), is("Error when calling the external service " + EXPECTED_AFP_HISTORIKK_IDENTIFIER + " in " + PEN
                + ". An error occurred in the provider, received 500 INTERNAL SERVER ERROR"));
    }

    @Test
    void should_throw_FailedCallingExternalServiceException_when_400_from_getUttaksgradForVedtak() {
        when(restTemplateMock.exchange(eq(AFP_HISTORIKK_URL), any(), any(), eq(AfpHistorikkDto.class)))
                .thenThrow(new RestClientResponseException("", HttpStatus.BAD_REQUEST.value(), "", null, null, null));

        FailedCallingExternalServiceException thrown = assertThrows(
                FailedCallingExternalServiceException.class,
                () -> consumer.getAfpHistorikkForPerson(""));

        assertThat(thrown.getMessage(), is("Error when calling the external service " + EXPECTED_AFP_HISTORIKK_IDENTIFIER + " in " + PEN + ". Received 400 BAD REQUEST"));
    }

    @Test
    void should_throw_FailedCallingExternalServiceException_when_RuntimeException_from_getUttaksgradForVedtak() {
        when(restTemplateMock.exchange(eq(AFP_HISTORIKK_URL), any(), any(), eq(AfpHistorikkDto.class)))
                .thenThrow(new RuntimeException());

        FailedCallingExternalServiceException thrown = assertThrows(
                FailedCallingExternalServiceException.class,
                () -> consumer.getAfpHistorikkForPerson(""));

        assertThat(thrown.getMessage(), is("Error when calling the external service " + EXPECTED_AFP_HISTORIKK_IDENTIFIER + " in " + PEN + ". An error occurred in the consumer"));
    }

    @Test
    void should_throw_FailedCallingExternalServiceException_when_401_from_getAlderSakUttaksgradhistorikkForPerson() {
        when(restTemplateMock.exchange(eq(UFOREHISTORIKK_URL), any(), any(), eq(UforeHistorikkDto.class)))
                .thenThrow(new RestClientResponseException("", HttpStatus.UNAUTHORIZED.value(), "", null, null, null));

        FailedCallingExternalServiceException thrown = assertThrows(
                FailedCallingExternalServiceException.class,
                () -> consumer.getUforeHistorikkForPerson(""));

        assertThat(thrown.getMessage(), is("Error when calling the external service " + EXPECTED_UFOREHISTORIKK_IDENTIFIER + " in " + PEN + ". Received 401 UNAUTHORIZED"));
    }

    @Test
    void should_throw_FailedCallingExternalServiceException_when_500_from_getAlderSakUttaksgradhistorikkForPerson() {
        when(restTemplateMock.exchange(eq(UFOREHISTORIKK_URL), any(), any(), eq(UforeHistorikkDto.class)))
                .thenThrow(new RestClientResponseException("", HttpStatus.INTERNAL_SERVER_ERROR.value(), "", null, null, null));

        FailedCallingExternalServiceException thrown = assertThrows(
                FailedCallingExternalServiceException.class,
                () -> consumer.getUforeHistorikkForPerson(""));

        assertThat(thrown.getMessage(), is("Error when calling the external service " + EXPECTED_UFOREHISTORIKK_IDENTIFIER + " in " + PEN
                + ". An error occurred in the provider, received 500 INTERNAL SERVER ERROR"));
    }

    @Test
    void should_throw_FailedCallingExternalServiceException_when_400_from_getAlderSakUttaksgradhistorikkForPerson() {
        when(restTemplateMock.exchange(eq(UFOREHISTORIKK_URL), any(), any(), eq(UforeHistorikkDto.class)))
                .thenThrow(new RestClientResponseException("", HttpStatus.BAD_REQUEST.value(), "", null, null, null));

        FailedCallingExternalServiceException thrown = assertThrows(
                FailedCallingExternalServiceException.class,
                () -> consumer.getUforeHistorikkForPerson(""));

        assertThat(thrown.getMessage(), is("Error when calling the external service " + EXPECTED_UFOREHISTORIKK_IDENTIFIER + " in " + PEN + ". Received 400 BAD REQUEST"));
    }

    @Test
    void should_throw_FailedCallingExternalServiceException_when_RuntimeException_from_getAlderSakUttaksgradhistorikkForPerson() {
        when(restTemplateMock.exchange(eq(UFOREHISTORIKK_URL), any(), any(), eq(UforeHistorikkDto.class)))
                .thenThrow(new RuntimeException());

        FailedCallingExternalServiceException thrown = assertThrows(
                FailedCallingExternalServiceException.class,
                () -> consumer.getUforeHistorikkForPerson(""));

        assertThat(thrown.getMessage(),
                is("Error when calling the external service " + EXPECTED_UFOREHISTORIKK_IDENTIFIER + " in " + PEN + ". An error occurred in the consumer"));
    }
}
