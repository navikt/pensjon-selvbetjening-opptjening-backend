package no.nav.pensjon.selvbetjeningopptjening.consumer.pensjonsbeholdning;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import static no.nav.pensjon.selvbetjeningopptjening.consumer.pensjonsbeholdning.PensjonsbeholdningConsumer.CONSUMED_SERVICE;
import static no.nav.pensjon.selvbetjeningopptjening.util.Constants.POPP;

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
import no.nav.pensjon.selvbetjeningopptjening.model.BeholdningDto;

@ExtendWith(MockitoExtension.class)
class PensjonsbeholdningConsumerTest {

    private static final String ENDPOINT = "http://poppEndpoint.test";
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
        BeholdningListeResponse expectedResponse = new BeholdningListeResponse();
        List<BeholdningDto> expectedBeholdningList = List.of(new BeholdningDto());
        expectedResponse.setBeholdninger(expectedBeholdningList);

        ResponseEntity<BeholdningListeResponse> expectedResponseEntity = new ResponseEntity<>(expectedResponse, null, HttpStatus.OK);

        when(restTemplateMock.exchange(urlCaptor.capture(), any(), any(), eq(BeholdningListeResponse.class))).thenReturn(expectedResponseEntity);

        List<BeholdningDto> actualBeholdningList = consumer.getPensjonsbeholdning("fnr");

        assertThat(actualBeholdningList, is(expectedBeholdningList));
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

        FailedCallingExternalServiceException thrown = assertThrows(
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

        FailedCallingExternalServiceException thrown = assertThrows(
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

        FailedCallingExternalServiceException thrown = assertThrows(
                FailedCallingExternalServiceException.class,
                () -> consumer.getPensjonsbeholdning(""));

        assertThat(thrown.getMessage(),
                is("Error when calling the external service " + CONSUMED_SERVICE + " in " + POPP
                        + ". An error occurred in the provider, received 500 INTERNAL SERVER ERROR"));
    }

    @Test
    void should_return_FailedCallingExternalServiceException_when_RuntimeException() {
        when(restTemplateMock.exchange(eq("http://poppEndpoint.test/beholdning"),
                eq(HttpMethod.POST),
                httpEntityCaptor.capture(),
                eq(BeholdningListeResponse.class))).thenThrow(new RuntimeException());

        FailedCallingExternalServiceException thrown = assertThrows(
                FailedCallingExternalServiceException.class,
                () -> consumer.getPensjonsbeholdning(""));

        assertThat(thrown.getMessage(),
                is("Error when calling the external service " + CONSUMED_SERVICE + " in " + POPP + ". An error occurred in the consumer"));
    }
}
