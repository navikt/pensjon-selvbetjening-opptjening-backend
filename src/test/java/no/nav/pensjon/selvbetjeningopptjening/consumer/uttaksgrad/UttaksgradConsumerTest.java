package no.nav.pensjon.selvbetjeningopptjening.consumer.uttaksgrad;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import static no.nav.pensjon.selvbetjeningopptjening.util.Constants.PEN;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

import no.nav.pensjon.selvbetjeningopptjening.consumer.FailedCallingExternalServiceException;
import no.nav.pensjon.selvbetjeningopptjening.model.Uttaksgrad;

@ExtendWith(MockitoExtension.class)
public class UttaksgradConsumerTest {

    private final String endpoint = "http://penEndpoint.test";

    @Mock
    private RestTemplate restTemplateMock;

    @Captor
    private ArgumentCaptor<String> urlCaptor;

    @Captor
    private ArgumentCaptor<HttpMethod> httpMethodCaptor;

    @Captor
    private ArgumentCaptor<HttpEntity<Object>> httpEntityCaptor;

    private UttaksgradConsumer consumer;

    @BeforeEach
    public void setup() {
        consumer = new UttaksgradConsumer(endpoint);
        consumer.setRestTemplate(restTemplateMock);
    }

    @Test
    public void should_return_list_of_uttaksgrad_when_getUttaksgradForVedtak() {
        UttaksgradListResponse expectedResponse = new UttaksgradListResponse();
        List<Uttaksgrad> expectedUttaksgradList = List.of(new Uttaksgrad());
        expectedResponse.setUttaksgradList(expectedUttaksgradList);

        ResponseEntity<UttaksgradListResponse> expectedResponseEntity = new ResponseEntity<>(expectedResponse, HttpStatus.OK);

        when(restTemplateMock.exchange(eq(endpoint + "/uttaksgrad/search"), any(), any(), eq(UttaksgradListResponse.class))).thenReturn(expectedResponseEntity);

        List<Uttaksgrad> actualUttaksgradList = consumer.getUttaksgradForVedtak(new ArrayList<>());

        assertThat(actualUttaksgradList, is(expectedUttaksgradList));
    }

    @Test
    public void should_add_vedtakid_as_queryparam_in_GET_when_getUttaksgradForVedtak() {
        List<Long> expectedVedtakIds = List.of(111L, 222L);

        when(restTemplateMock.exchange(urlCaptor.capture(), httpMethodCaptor.capture(), any(), eq(UttaksgradListResponse.class))).thenReturn(new ResponseEntity<>(HttpStatus.OK));

        consumer.getUttaksgradForVedtak(expectedVedtakIds);

        assertThat(httpMethodCaptor.getValue(), is(HttpMethod.GET));
        assertThat(urlCaptor.getValue(), is(endpoint + "/uttaksgrad/search?vedtakId=" + expectedVedtakIds.get(0) + "&vedtakId=" + expectedVedtakIds.get(1)));
    }

    @Test
    public void should_get_list_of_uttaksgrad_when_getAlderSakUttaksgradhistorikkForPerson() {
        UttaksgradListResponse expectedResponse = new UttaksgradListResponse();
        List<Uttaksgrad> expectedUttaksgradList = List.of(new Uttaksgrad());
        expectedResponse.setUttaksgradList(expectedUttaksgradList);

        ResponseEntity<UttaksgradListResponse> expectedResponseEntity = new ResponseEntity<>(expectedResponse, HttpStatus.OK);

        when(restTemplateMock.exchange(eq(endpoint + "/uttaksgrad/person?sakType=ALDER"), any(), any(), eq(UttaksgradListResponse.class))).thenReturn(expectedResponseEntity);

        List<Uttaksgrad> actualUttaksgradList = consumer.getAlderSakUttaksgradhistorikkForPerson("");

        assertThat(actualUttaksgradList, is(expectedUttaksgradList));
    }

    @Test
    public void should_add_fnr_as_headerparam_and_add_queryparam_ALDER_in_GET_when_getAlderSakUttaksgradhistorikkForPerson() {
        String expectedFnr = "expFnr";

        when(restTemplateMock.exchange(urlCaptor.capture(), httpMethodCaptor.capture(), httpEntityCaptor.capture(), eq(UttaksgradListResponse.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        consumer.getAlderSakUttaksgradhistorikkForPerson(expectedFnr);

        assertThat(httpMethodCaptor.getValue(), is(HttpMethod.GET));
        assertThat(urlCaptor.getValue(), is(endpoint + "/uttaksgrad/person?sakType=ALDER"));
        assertThat("Request should contain header pid", httpEntityCaptor.getValue().getHeaders().containsKey("pid"), is(true));
        assertThat(Objects.requireNonNull(httpEntityCaptor.getValue().getHeaders().get("pid")).get(0), is(expectedFnr));
    }

    @Test
    public void should_throw_FailedCallingExternalServiceException_when_401_from_getUttaksgradForVedtak() {
        when(restTemplateMock.exchange(eq(endpoint + "/uttaksgrad/search"), any(), any(), eq(UttaksgradListResponse.class)))
                .thenThrow(new RestClientResponseException("", HttpStatus.UNAUTHORIZED.value(), "", null, null, null));

        FailedCallingExternalServiceException thrown = assertThrows(
                FailedCallingExternalServiceException.class,
                () -> consumer.getUttaksgradForVedtak(new ArrayList<>()));

        assertThat(thrown.getMessage(), is("Error when calling the external service PROPEN3000 getUttaksgradForVedtak in " + PEN + ". Received 401 UNAUTHORIZED"));
    }

    @Test
    public void should_throw_FailedCallingExternalServiceException_when_500_from_getUttaksgradForVedtak() {
        when(restTemplateMock.exchange(eq(endpoint + "/uttaksgrad/search"), any(), any(), eq(UttaksgradListResponse.class)))
                .thenThrow(new RestClientResponseException("", HttpStatus.INTERNAL_SERVER_ERROR.value(), "", null, null, null));

        FailedCallingExternalServiceException thrown = assertThrows(
                FailedCallingExternalServiceException.class,
                () -> consumer.getUttaksgradForVedtak(new ArrayList<>()));

        assertThat(thrown.getMessage(), is("Error when calling the external service PROPEN3000 getUttaksgradForVedtak in " + PEN
                + ". An error occurred in the provider, received 500 INTERNAL SERVER ERROR"));
    }

    @Test
    public void should_throw_FailedCallingExternalServiceException_when_400_from_getUttaksgradForVedtak() {
        when(restTemplateMock.exchange(eq(endpoint + "/uttaksgrad/search"), any(), any(), eq(UttaksgradListResponse.class)))
                .thenThrow(new RestClientResponseException("", HttpStatus.BAD_REQUEST.value(), "", null, null, null));

        FailedCallingExternalServiceException thrown = assertThrows(
                FailedCallingExternalServiceException.class,
                () -> consumer.getUttaksgradForVedtak(new ArrayList<>()));

        assertThat(thrown.getMessage(), is("Error when calling the external service PROPEN3000 getUttaksgradForVedtak in " + PEN + ". Received 400 BAD REQUEST"));
    }

    @Test
    public void should_throw_FailedCallingExternalServiceException_when_RuntimeException_from_getUttaksgradForVedtak() {
        when(restTemplateMock.exchange(eq(endpoint + "/uttaksgrad/search"), any(), any(), eq(UttaksgradListResponse.class)))
                .thenThrow(new RuntimeException());

        FailedCallingExternalServiceException thrown = assertThrows(
                FailedCallingExternalServiceException.class,
                () -> consumer.getUttaksgradForVedtak(new ArrayList<>()));

        assertThat(thrown.getMessage(), is("Error when calling the external service PROPEN3000 getUttaksgradForVedtak in " + PEN + ". An error occurred in the consumer"));
    }

    @Test
    public void should_throw_FailedCallingExternalServiceException_when_401_from_getAlderSakUttaksgradhistorikkForPerson() {
        when(restTemplateMock.exchange(eq(endpoint + "/uttaksgrad/person?sakType=ALDER"), any(), any(), eq(UttaksgradListResponse.class)))
                .thenThrow(new RestClientResponseException("", HttpStatus.UNAUTHORIZED.value(), "", null, null, null));

        FailedCallingExternalServiceException thrown = assertThrows(
                FailedCallingExternalServiceException.class,
                () -> consumer.getAlderSakUttaksgradhistorikkForPerson(""));

        assertThat(thrown.getMessage(), is("Error when calling the external service PROPEN3001 getAlderSakUttaksgradhistorikkForPerson in " + PEN + ". Received 401 UNAUTHORIZED"));
    }

    @Test
    public void should_throw_FailedCallingExternalServiceException_when_500_from_getAlderSakUttaksgradhistorikkForPerson() {
        when(restTemplateMock.exchange(eq(endpoint + "/uttaksgrad/person?sakType=ALDER"), any(), any(), eq(UttaksgradListResponse.class)))
                .thenThrow(new RestClientResponseException("", HttpStatus.INTERNAL_SERVER_ERROR.value(), "", null, null, null));

        FailedCallingExternalServiceException thrown = assertThrows(
                FailedCallingExternalServiceException.class,
                () -> consumer.getAlderSakUttaksgradhistorikkForPerson(""));

        assertThat(thrown.getMessage(), is("Error when calling the external service PROPEN3001 getAlderSakUttaksgradhistorikkForPerson in " + PEN
                + ". An error occurred in the provider, received 500 INTERNAL SERVER ERROR"));
    }

    @Test
    public void should_throw_FailedCallingExternalServiceException_when_400_from_getAlderSakUttaksgradhistorikkForPerson() {
        when(restTemplateMock.exchange(eq(endpoint + "/uttaksgrad/person?sakType=ALDER"), any(), any(), eq(UttaksgradListResponse.class)))
                .thenThrow(new RestClientResponseException("", HttpStatus.BAD_REQUEST.value(), "", null, null, null));

        FailedCallingExternalServiceException thrown = assertThrows(
                FailedCallingExternalServiceException.class,
                () -> consumer.getAlderSakUttaksgradhistorikkForPerson(""));

        assertThat(thrown.getMessage(), is("Error when calling the external service PROPEN3001 getAlderSakUttaksgradhistorikkForPerson in " + PEN + ". Received 400 BAD REQUEST"));
    }

    @Test
    public void should_throw_FailedCallingExternalServiceException_when_RuntimeException_from_getAlderSakUttaksgradhistorikkForPerson() {
        when(restTemplateMock.exchange(eq(endpoint + "/uttaksgrad/person?sakType=ALDER"), any(), any(), eq(UttaksgradListResponse.class)))
                .thenThrow(new RuntimeException());

        FailedCallingExternalServiceException thrown = assertThrows(
                FailedCallingExternalServiceException.class,
                () -> consumer.getAlderSakUttaksgradhistorikkForPerson(""));

        assertThat(thrown.getMessage(),
                is("Error when calling the external service PROPEN3001 getAlderSakUttaksgradhistorikkForPerson in " + PEN + ". An error occurred in the consumer"));
    }
}