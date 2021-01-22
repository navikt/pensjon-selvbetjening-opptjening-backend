package no.nav.pensjon.selvbetjeningopptjening.consumer.uttaksgrad;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import static no.nav.pensjon.selvbetjeningopptjening.util.Constants.PEN;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import no.nav.pensjon.selvbetjeningopptjening.opptjening.Uttaksgrad;
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
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import no.nav.pensjon.selvbetjeningopptjening.consumer.FailedCallingExternalServiceException;
import no.nav.pensjon.selvbetjeningopptjening.model.UttaksgradDto;

@ExtendWith(MockitoExtension.class)
class UttaksgradConsumerTest {

    private static final String ENDPOINT = "http://penEndpoint.test";
    private static final String PERSON_ENDPOINT_PATH = ENDPOINT + "/uttaksgrad/person?sakType=ALDER";
    private static final String SEARCH_ENDPOINT_PATH = ENDPOINT + "/uttaksgrad/search";
    private static final LocalDate DATE = LocalDate.of(1991, 1, 1);
    private UttaksgradConsumer consumer;

    @Mock
    private RestTemplate rest;
    @Captor
    private ArgumentCaptor<String> urlCaptor;
    @Captor
    private ArgumentCaptor<HttpMethod> httpMethodCaptor;
    @Captor
    private ArgumentCaptor<HttpEntity<Object>> httpEntityCaptor;

    @BeforeEach
    void setUp() {
        consumer = new UttaksgradConsumer(ENDPOINT);
        consumer.setRestTemplate(rest);
    }

    @Test
    void should_return_listOfUttaksgrad_when_getUttaksgradForVedtak() {
        var expectedResponse = new UttaksgradListResponse();
        var uttaksgradDto = new UttaksgradDto();
        uttaksgradDto.setFomDato(DATE);
        expectedResponse.setUttaksgradList(List.of(uttaksgradDto));
        ResponseEntity<UttaksgradListResponse> entity = new ResponseEntity<>(expectedResponse, HttpStatus.OK);
        when(rest.exchange(eq(SEARCH_ENDPOINT_PATH), any(), any(), eq(UttaksgradListResponse.class))).thenReturn(entity);

        List<Uttaksgrad> uttaksgrader = consumer.getUttaksgradForVedtak(new ArrayList<>());

        assertEquals(1, uttaksgrader.size());
        assertEquals(DATE, uttaksgrader.get(0).getFomDate());
    }

    @Test
    void should_add_vedtakid_as_queryparam_in_GET_when_getUttaksgradForVedtak() {
        List<Long> expectedVedtakIds = List.of(111L, 222L);
        when(rest.exchange(urlCaptor.capture(), httpMethodCaptor.capture(), any(), eq(UttaksgradListResponse.class))).thenReturn(new ResponseEntity<>(HttpStatus.OK));

        consumer.getUttaksgradForVedtak(expectedVedtakIds);

        assertThat(httpMethodCaptor.getValue(), is(HttpMethod.GET));
        assertThat(urlCaptor.getValue(), is(SEARCH_ENDPOINT_PATH + "?vedtakId=" + expectedVedtakIds.get(0) + "&vedtakId=" + expectedVedtakIds.get(1)));
    }

    @Test
    void should_get_list_of_uttaksgrad_when_getAlderSakUttaksgradhistorikkForPerson() {
        var response = new UttaksgradListResponse();
        var uttaksgradDto = new UttaksgradDto();
        uttaksgradDto.setFomDato(DATE);
        response.setUttaksgradList(List.of(uttaksgradDto));
        ResponseEntity<UttaksgradListResponse> entity = new ResponseEntity<>(response, HttpStatus.OK);
        when(rest.exchange(eq(PERSON_ENDPOINT_PATH), any(), any(), eq(UttaksgradListResponse.class))).thenReturn(entity);

        List<Uttaksgrad> uttaksgrader = consumer.getAlderSakUttaksgradhistorikkForPerson("");

        assertEquals(1, uttaksgrader.size());
        assertEquals(DATE, uttaksgrader.get(0).getFomDate());
    }

    @Test
    void should_add_fnr_as_headerparam_and_add_queryparam_ALDER_in_GET_when_getAlderSakUttaksgradhistorikkForPerson() {
        String expectedFnr = "expFnr";

        when(rest.exchange(urlCaptor.capture(), httpMethodCaptor.capture(), httpEntityCaptor.capture(), eq(UttaksgradListResponse.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        consumer.getAlderSakUttaksgradhistorikkForPerson(expectedFnr);

        assertThat(httpMethodCaptor.getValue(), is(HttpMethod.GET));
        assertThat(urlCaptor.getValue(), is(PERSON_ENDPOINT_PATH));
        assertThat("Request should contain header pid", httpEntityCaptor.getValue().getHeaders().containsKey("pid"), is(true));
        assertThat(Objects.requireNonNull(httpEntityCaptor.getValue().getHeaders().get("pid")).get(0), is(expectedFnr));
    }

    @Test
    void should_throw_FailedCallingExternalServiceException_when_401_from_getUttaksgradForVedtak() {
        when(rest.exchange(eq(SEARCH_ENDPOINT_PATH), any(), any(), eq(UttaksgradListResponse.class)))
                .thenThrow(new RestClientResponseException("", HttpStatus.UNAUTHORIZED.value(), "", null, null, null));

        var thrown = assertThrows(
                FailedCallingExternalServiceException.class,
                () -> consumer.getUttaksgradForVedtak(new ArrayList<>()));

        assertThat(thrown.getMessage(), is("Error when calling the external service PROPEN3000 getUttaksgradForVedtak in " + PEN + ". Received 401 UNAUTHORIZED"));
    }

    @Test
    void should_throw_FailedCallingExternalServiceException_when_500_from_getUttaksgradForVedtak() {
        when(rest.exchange(eq(SEARCH_ENDPOINT_PATH), any(), any(), eq(UttaksgradListResponse.class)))
                .thenThrow(new RestClientResponseException("", HttpStatus.INTERNAL_SERVER_ERROR.value(), "", null, null, null));

        var thrown = assertThrows(
                FailedCallingExternalServiceException.class,
                () -> consumer.getUttaksgradForVedtak(new ArrayList<>()));

        assertThat(thrown.getMessage(), is("Error when calling the external service PROPEN3000 getUttaksgradForVedtak in " + PEN
                + ". An error occurred in the provider, received 500 INTERNAL SERVER ERROR"));
    }

    @Test
    void should_throw_FailedCallingExternalServiceException_when_400_from_getUttaksgradForVedtak() {
        when(rest.exchange(eq(SEARCH_ENDPOINT_PATH), any(), any(), eq(UttaksgradListResponse.class)))
                .thenThrow(new RestClientResponseException("", HttpStatus.BAD_REQUEST.value(), "", null, null, null));

        var thrown = assertThrows(
                FailedCallingExternalServiceException.class,
                () -> consumer.getUttaksgradForVedtak(new ArrayList<>()));

        assertThat(thrown.getMessage(), is("Error when calling the external service PROPEN3000 getUttaksgradForVedtak in " + PEN + ". Received 400 BAD REQUEST"));
    }

    @Test
    void should_throw_FailedCallingExternalServiceException_when_RestClientException_from_getUttaksgradForVedtak() {
        when(rest.exchange(eq(SEARCH_ENDPOINT_PATH), any(), any(), eq(UttaksgradListResponse.class)))
                .thenThrow(new RestClientException("oops"));

        var thrown = assertThrows(
                FailedCallingExternalServiceException.class,
                () -> consumer.getUttaksgradForVedtak(new ArrayList<>()));

        assertThat(thrown.getMessage(), is("Error when calling the external service PROPEN3000 getUttaksgradForVedtak in " + PEN + ". Failed to access service"));
    }

    @Test
    void should_throw_FailedCallingExternalServiceException_when_401_from_getAlderSakUttaksgradhistorikkForPerson() {
        when(rest.exchange(eq(PERSON_ENDPOINT_PATH), any(), any(), eq(UttaksgradListResponse.class)))
                .thenThrow(new RestClientResponseException("", HttpStatus.UNAUTHORIZED.value(), "", null, null, null));

        var thrown = assertThrows(
                FailedCallingExternalServiceException.class,
                () -> consumer.getAlderSakUttaksgradhistorikkForPerson(""));

        assertThat(thrown.getMessage(), is("Error when calling the external service PROPEN3001 getAlderSakUttaksgradhistorikkForPerson in " + PEN + ". Received 401 UNAUTHORIZED"));
    }

    @Test
    void should_throw_FailedCallingExternalServiceException_when_500_from_getAlderSakUttaksgradhistorikkForPerson() {
        when(rest.exchange(eq(PERSON_ENDPOINT_PATH), any(), any(), eq(UttaksgradListResponse.class)))
                .thenThrow(new RestClientResponseException("", HttpStatus.INTERNAL_SERVER_ERROR.value(), "", null, null, null));

        var thrown = assertThrows(
                FailedCallingExternalServiceException.class,
                () -> consumer.getAlderSakUttaksgradhistorikkForPerson(""));

        assertThat(thrown.getMessage(), is("Error when calling the external service PROPEN3001 getAlderSakUttaksgradhistorikkForPerson in " + PEN
                + ". An error occurred in the provider, received 500 INTERNAL SERVER ERROR"));
    }

    @Test
    void should_throw_FailedCallingExternalServiceException_when_400_from_getAlderSakUttaksgradhistorikkForPerson() {
        when(rest.exchange(eq(PERSON_ENDPOINT_PATH), any(), any(), eq(UttaksgradListResponse.class)))
                .thenThrow(new RestClientResponseException("", HttpStatus.BAD_REQUEST.value(), "", null, null, null));

        var thrown = assertThrows(
                FailedCallingExternalServiceException.class,
                () -> consumer.getAlderSakUttaksgradhistorikkForPerson(""));

        assertThat(thrown.getMessage(), is("Error when calling the external service PROPEN3001 getAlderSakUttaksgradhistorikkForPerson in " + PEN + ". Received 400 BAD REQUEST"));
    }

    @Test
    void should_throw_FailedCallingExternalServiceException_when_RestClientException_from_getAlderSakUttaksgradhistorikkForPerson() {
        when(rest.exchange(eq(PERSON_ENDPOINT_PATH), any(), any(), eq(UttaksgradListResponse.class)))
                .thenThrow(new RestClientException("oops"));

        var thrown = assertThrows(
                FailedCallingExternalServiceException.class,
                () -> consumer.getAlderSakUttaksgradhistorikkForPerson(""));

        assertThat(thrown.getMessage(),
                is("Error when calling the external service PROPEN3001 getAlderSakUttaksgradhistorikkForPerson in " + PEN + ". Failed to access service"));
    }
}
