package no.nav.pensjon.selvbetjeningopptjening.consumer.pensjonsbeholdning;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import static no.nav.pensjon.selvbetjeningopptjening.consumer.pensjonsbeholdning.PensjonsbeholdningConsumer.CONSUMED_SERVICE;
import static no.nav.pensjon.selvbetjeningopptjening.util.Constants.POPP;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import no.nav.pensjon.selvbetjeningopptjening.opptjening.Beholdning;
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
import no.nav.pensjon.selvbetjeningopptjening.model.BeholdningDto;

@ExtendWith(MockitoExtension.class)
class PensjonsbeholdningConsumerTest {

    private static final String ENDPOINT = "http://poppEndpoint.test";
    private static LocalDate DATE = LocalDate.of(1991, 1, 1);
    private PensjonsbeholdningConsumer consumer;

    @Mock
    private RestTemplate restTemplateMock;
    @Captor
    private ArgumentCaptor<String> urlCaptor;
    @Captor
    private ArgumentCaptor<HttpEntity<BeholdningListeRequest>> httpEntityCaptor;
    @Captor
    private ArgumentCaptor<HttpMethod> httpMethodCaptor;

    @BeforeEach
    void setup() {
        consumer = new PensjonsbeholdningConsumer(ENDPOINT, restTemplateMock);
    }

    @Test
    void should_return_list_of_Beholdning_when_getPensjonsbeholdning() {
        var response = new BeholdningListeResponse();
        response.setBeholdninger(List.of(beholdningDto()));
        ResponseEntity<BeholdningListeResponse> entity = new ResponseEntity<>(response, null, HttpStatus.OK);
        when(restTemplateMock.exchange(urlCaptor.capture(), any(), any(), eq(BeholdningListeResponse.class))).thenReturn(entity);

        List<Beholdning> beholdninger = consumer.getPensjonsbeholdning("fnr");

        assertEquals(1, beholdninger.size());
        assertEquals(DATE, beholdninger.get(0).getFomDato());
    }

    @Test
    void should_add_fnr_as_headerparam_when_POST_getPensjonsbeholdning() {
        String expectedFnr = "fnrValue";

        when(restTemplateMock.exchange(urlCaptor.capture(), httpMethodCaptor.capture(), httpEntityCaptor.capture(), eq(BeholdningListeResponse.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        consumer.getPensjonsbeholdning(expectedFnr);

        assertThat(httpMethodCaptor.getValue(), is(HttpMethod.POST));
        assertThat(urlCaptor.getValue(), is(ENDPOINT + "/beholdning"));
        assertThat(Objects.requireNonNull(httpEntityCaptor.getValue().getBody()).getFnr(), is(expectedFnr));
    }

    @Test
    void should_return_FailedCallingExternalServiceException_when_401() {
        when(restTemplateMock.exchange(eq("http://poppEndpoint.test/beholdning"),
                eq(HttpMethod.POST),
                httpEntityCaptor.capture(),
                eq(BeholdningListeResponse.class))).thenThrow(new RestClientResponseException("", HttpStatus.UNAUTHORIZED.value(), "", null, null, null));

        var thrown = assertThrows(
                FailedCallingExternalServiceException.class,
                () -> consumer.getPensjonsbeholdning(""));

        assertThat(thrown.getMessage(), is("Error when calling the external service " + CONSUMED_SERVICE + " in " + POPP + ". Received 401 UNAUTHORIZED"));
    }

    @Test
    void should_return_FailedCallingExternalServiceException_when_512() {
        when(restTemplateMock.exchange(eq("http://poppEndpoint.test/beholdning"),
                eq(HttpMethod.POST),
                httpEntityCaptor.capture(),
                eq(BeholdningListeResponse.class))).thenThrow(new RestClientResponseException("PersonDoesNotExistExceptionDto", 512, "", null, null, null));

        var thrown = assertThrows(
                FailedCallingExternalServiceException.class,
                () -> consumer.getPensjonsbeholdning(""));

        assertThat(thrown.getMessage(), is("Error when calling the external service " + CONSUMED_SERVICE + " in " + POPP + ". Person ikke funnet"));
    }

    @Test
    void should_return_FailedCallingExternalServiceException_when_500() {
        when(restTemplateMock.exchange(eq("http://poppEndpoint.test/beholdning"),
                eq(HttpMethod.POST),
                httpEntityCaptor.capture(),
                eq(BeholdningListeResponse.class))).thenThrow(new RestClientResponseException("", HttpStatus.INTERNAL_SERVER_ERROR.value(), "", null, null, null));

        var thrown = assertThrows(
                FailedCallingExternalServiceException.class,
                () -> consumer.getPensjonsbeholdning(""));

        assertThat(thrown.getMessage(),
                is("Error when calling the external service " + CONSUMED_SERVICE + " in " + POPP
                        + ". An error occurred in the provider, received 500 INTERNAL SERVER ERROR"));
    }

    @Test
    void should_return_FailedCallingExternalServiceException_when_RestClientException() {
        when(restTemplateMock.exchange(eq("http://poppEndpoint.test/beholdning"),
                eq(HttpMethod.POST),
                httpEntityCaptor.capture(),
                eq(BeholdningListeResponse.class))).thenThrow(new RestClientException("oops"));

        var thrown = assertThrows(
                FailedCallingExternalServiceException.class,
                () -> consumer.getPensjonsbeholdning(""));

        assertThat(thrown.getMessage(),
                is("Error when calling the external service " + CONSUMED_SERVICE + " in " + POPP + ". Failed to access service"));
    }

    private static BeholdningDto beholdningDto() {
        var dto = new BeholdningDto();
        dto.setFomDato(DATE);
        return dto;
    }
}
