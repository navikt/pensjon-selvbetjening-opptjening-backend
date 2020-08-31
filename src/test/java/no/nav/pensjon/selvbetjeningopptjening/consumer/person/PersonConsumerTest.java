package no.nav.pensjon.selvbetjeningopptjening.consumer.person;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import static no.nav.pensjon.selvbetjeningopptjening.util.Constants.PEN;

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
import no.nav.pensjon.selvbetjeningopptjening.model.AfpHistorikk;
import no.nav.pensjon.selvbetjeningopptjening.model.UforeHistorikk;

@ExtendWith(MockitoExtension.class)
public class PersonConsumerTest {
    private final String endpoint = "http://penEndpoint.test";
    private final String expectedAfphistorikkIdentifier = "PROPEN2602 getAfphistorikkForPerson";
    private final String expectedUforehistorikkIdentifier = "PROPEN2603 getUforehistorikkForPerson";

    @Mock
    private RestTemplate restTemplateMock;

    @Captor
    private ArgumentCaptor<String> urlCaptor;

    @Captor
    private ArgumentCaptor<HttpMethod> httpMethodCaptor;

    @Captor
    private ArgumentCaptor<HttpEntity<Object>> httpEntityCaptor;

    private PersonConsumer consumer;

    @BeforeEach
    public void setup() {
        consumer = new PersonConsumer(endpoint);
        consumer.setRestTemplate(restTemplateMock);
    }

    @Test
    public void should_return_Afphistorikk_when_getAfpHistorikkForPerson() {
        AfpHistorikk expectedResponse = new AfpHistorikk();

        ResponseEntity<AfpHistorikk> expectedResponseEntity = new ResponseEntity<>(expectedResponse, HttpStatus.OK);

        when(restTemplateMock.exchange(eq(endpoint + "/person/afphistorikk"), any(), any(), eq(AfpHistorikk.class))).thenReturn(expectedResponseEntity);

        AfpHistorikk actualResponse = consumer.getAfpHistorikkForPerson("");

        assertThat(actualResponse, is(expectedResponse));
    }

    @Test
    public void should_add_fnr_as_headerparam_in_GET_when_getAfpHistorikkForPerson() {
        String expectedFnr = "expFnr";

        when(restTemplateMock.exchange(urlCaptor.capture(), httpMethodCaptor.capture(), httpEntityCaptor.capture(), eq(AfpHistorikk.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        consumer.getAfpHistorikkForPerson(expectedFnr);

        assertThat(httpMethodCaptor.getValue(), is(HttpMethod.GET));
        assertThat(urlCaptor.getValue(), is(endpoint + "/person/afphistorikk"));
        assertThat("Request should contain header pid", httpEntityCaptor.getValue().getHeaders().containsKey("pid"), is(true));
        assertThat(Objects.requireNonNull(httpEntityCaptor.getValue().getHeaders().get("pid")).get(0), is(expectedFnr));
    }

    @Test
    public void should_get_Uforehistorikk_when_getUforeHistorikkForPerson() {
        UforeHistorikk expectedResponse = new UforeHistorikk();

        ResponseEntity<UforeHistorikk> expectedResponseEntity = new ResponseEntity<>(expectedResponse, HttpStatus.OK);

        when(restTemplateMock.exchange(eq(endpoint + "/person/uforehistorikk"), any(), any(), eq(UforeHistorikk.class))).thenReturn(expectedResponseEntity);

        UforeHistorikk actualResponse = consumer.getUforeHistorikkForPerson("");

        assertThat(actualResponse, is(expectedResponse));
    }

    @Test
    public void should_add_fnr_as_headerparam_in_GET_when_getUforehistorikkForPerson() {
        String expectedFnr = "expFnr";

        when(restTemplateMock.exchange(urlCaptor.capture(), httpMethodCaptor.capture(), httpEntityCaptor.capture(), eq(UforeHistorikk.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        consumer.getUforeHistorikkForPerson(expectedFnr);

        assertThat(httpMethodCaptor.getValue(), is(HttpMethod.GET));
        assertThat(urlCaptor.getValue(), is(endpoint + "/person/uforehistorikk"));
        assertThat("Request should contain header pid", httpEntityCaptor.getValue().getHeaders().containsKey("pid"), is(true));
        assertThat(Objects.requireNonNull(httpEntityCaptor.getValue().getHeaders().get("pid")).get(0), is(expectedFnr));
    }

    @Test
    public void should_throw_FailedCallingExternalServiceException_when_401_from_getAfphistorikkForPerson() {
        when(restTemplateMock.exchange(eq(endpoint + "/person/afphistorikk"), any(), any(), eq(AfpHistorikk.class)))
                .thenThrow(new RestClientResponseException("", HttpStatus.UNAUTHORIZED.value(), "", null, null, null));

        FailedCallingExternalServiceException thrown = assertThrows(
                FailedCallingExternalServiceException.class,
                () -> consumer.getAfpHistorikkForPerson(""));

        assertThat(thrown.getMessage(), is("Error when calling the external service " + expectedAfphistorikkIdentifier + " in " + PEN + ". Received 401 UNAUTHORIZED"));
    }

    @Test
    public void should_throw_FailedCallingExternalServiceException_when_500_from_getUttaksgradForVedtak() {
        when(restTemplateMock.exchange(eq(endpoint + "/person/afphistorikk"), any(), any(), eq(AfpHistorikk.class)))
                .thenThrow(new RestClientResponseException("", HttpStatus.INTERNAL_SERVER_ERROR.value(), "", null, null, null));

        FailedCallingExternalServiceException thrown = assertThrows(
                FailedCallingExternalServiceException.class,
                () -> consumer.getAfpHistorikkForPerson(""));

        assertThat(thrown.getMessage(), is("Error when calling the external service " + expectedAfphistorikkIdentifier + " in " + PEN
                + ". An error occurred in the provider, received 500 INTERNAL SERVER ERROR"));
    }

    @Test
    public void should_throw_FailedCallingExternalServiceException_when_400_from_getUttaksgradForVedtak() {
        when(restTemplateMock.exchange(eq(endpoint + "/person/afphistorikk"), any(), any(), eq(AfpHistorikk.class)))
                .thenThrow(new RestClientResponseException("", HttpStatus.BAD_REQUEST.value(), "", null, null, null));

        FailedCallingExternalServiceException thrown = assertThrows(
                FailedCallingExternalServiceException.class,
                () -> consumer.getAfpHistorikkForPerson(""));

        assertThat(thrown.getMessage(), is("Error when calling the external service " + expectedAfphistorikkIdentifier + " in " + PEN + ". Received 400 BAD REQUEST"));
    }

    @Test
    public void should_throw_FailedCallingExternalServiceException_when_RuntimeException_from_getUttaksgradForVedtak() {
        when(restTemplateMock.exchange(eq(endpoint + "/person/afphistorikk"), any(), any(), eq(AfpHistorikk.class)))
                .thenThrow(new RuntimeException());

        FailedCallingExternalServiceException thrown = assertThrows(
                FailedCallingExternalServiceException.class,
                () -> consumer.getAfpHistorikkForPerson(""));

        assertThat(thrown.getMessage(), is("Error when calling the external service " + expectedAfphistorikkIdentifier + " in " + PEN + ". An error occurred in the consumer"));
    }

    @Test
    public void should_throw_FailedCallingExternalServiceException_when_401_from_getAlderSakUttaksgradhistorikkForPerson() {
        when(restTemplateMock.exchange(eq(endpoint + "/person/uforehistorikk"), any(), any(), eq(UforeHistorikk.class)))
                .thenThrow(new RestClientResponseException("", HttpStatus.UNAUTHORIZED.value(), "", null, null, null));

        FailedCallingExternalServiceException thrown = assertThrows(
                FailedCallingExternalServiceException.class,
                () -> consumer.getUforeHistorikkForPerson(""));

        assertThat(thrown.getMessage(), is("Error when calling the external service " + expectedUforehistorikkIdentifier + " in " + PEN + ". Received 401 UNAUTHORIZED"));
    }

    @Test
    public void should_throw_FailedCallingExternalServiceException_when_500_from_getAlderSakUttaksgradhistorikkForPerson() {
        when(restTemplateMock.exchange(eq(endpoint + "/person/uforehistorikk"), any(), any(), eq(UforeHistorikk.class)))
                .thenThrow(new RestClientResponseException("", HttpStatus.INTERNAL_SERVER_ERROR.value(), "", null, null, null));

        FailedCallingExternalServiceException thrown = assertThrows(
                FailedCallingExternalServiceException.class,
                () -> consumer.getUforeHistorikkForPerson(""));

        assertThat(thrown.getMessage(), is("Error when calling the external service " + expectedUforehistorikkIdentifier + " in " + PEN
                + ". An error occurred in the provider, received 500 INTERNAL SERVER ERROR"));
    }

    @Test
    public void should_throw_FailedCallingExternalServiceException_when_400_from_getAlderSakUttaksgradhistorikkForPerson() {
        when(restTemplateMock.exchange(eq(endpoint + "/person/uforehistorikk"), any(), any(), eq(UforeHistorikk.class)))
                .thenThrow(new RestClientResponseException("", HttpStatus.BAD_REQUEST.value(), "", null, null, null));

        FailedCallingExternalServiceException thrown = assertThrows(
                FailedCallingExternalServiceException.class,
                () -> consumer.getUforeHistorikkForPerson(""));

        assertThat(thrown.getMessage(), is("Error when calling the external service " + expectedUforehistorikkIdentifier + " in " + PEN + ". Received 400 BAD REQUEST"));
    }

    @Test
    public void should_throw_FailedCallingExternalServiceException_when_RuntimeException_from_getAlderSakUttaksgradhistorikkForPerson() {
        when(restTemplateMock.exchange(eq(endpoint + "/person/uforehistorikk"), any(), any(), eq(UforeHistorikk.class)))
                .thenThrow(new RuntimeException());

        FailedCallingExternalServiceException thrown = assertThrows(
                FailedCallingExternalServiceException.class,
                () -> consumer.getUforeHistorikkForPerson(""));

        assertThat(thrown.getMessage(),
                is("Error when calling the external service " + expectedUforehistorikkIdentifier + " in " + PEN + ". An error occurred in the consumer"));
    }
}