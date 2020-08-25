package no.nav.pensjon.selvbetjeningopptjening.consumer.opptjeningsgrunnlag;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import static no.nav.pensjon.selvbetjeningopptjening.consumer.opptjeningsgrunnlag.OpptjeningsgrunnlagConsumer.CONSUMED_SERVICE;
import static no.nav.pensjon.selvbetjeningopptjening.util.Constants.POPP;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import no.nav.pensjon.selvbetjeningopptjening.consumer.FailedCallingExternalServiceException;
import no.nav.pensjon.selvbetjeningopptjening.consumer.pensjonspoeng.PensjonspoengConsumer;
import no.nav.pensjon.selvbetjeningopptjening.model.Inntekt;
import no.nav.pensjon.selvbetjeningopptjening.model.OpptjeningsGrunnlag;

@ExtendWith(MockitoExtension.class)
public class OpptjeningsgrunnlagConsumerTest {

    private final String endpoint = "http://poppEndpoint.test";

    @Mock
    private RestTemplate restTemplateMock;

    private OpptjeningsgrunnlagConsumer consumer;

    @Captor
    private ArgumentCaptor<String> urlCaptor;

    @Captor
    private ArgumentCaptor<HttpMethod> httpMethodCaptor;

    @BeforeEach
    public void setup() {
        consumer = new OpptjeningsgrunnlagConsumer(endpoint);
        consumer.setRestTemplate(restTemplateMock);
    }

    @Test
    public void should_return_list_of_Inntekt_when_getInntektListeFromOpptjeningsgrunnlag() {
        HentOpptjeningsGrunnlagResponse expectedResponse = new HentOpptjeningsGrunnlagResponse();
        OpptjeningsGrunnlag expectedOpptjeningsGrunnlag = new OpptjeningsGrunnlag();
        List<Inntekt> expectedInntektList = List.of(new Inntekt());
        expectedOpptjeningsGrunnlag.setInntektListe(expectedInntektList);
        expectedResponse.setOpptjeningsGrunnlag(expectedOpptjeningsGrunnlag);

        ResponseEntity<HentOpptjeningsGrunnlagResponse> expectedResponseEntity = new ResponseEntity<>(expectedResponse, null, HttpStatus.OK);

        when(restTemplateMock.exchange(urlCaptor.capture(), any(), any(), eq(HentOpptjeningsGrunnlagResponse.class))).thenReturn(expectedResponseEntity);

        List<Inntekt> actualInntektList = consumer.getInntektListeFromOpptjeningsgrunnlag("fnr", 0, 0);

        assertThat(actualInntektList, is(expectedInntektList));
    }

    @Test
    public void should_add_fnr_as_pathparam_and_fom_and_tom_as_queryparams_when_GET_getInntektListeFromOpptjeningsgrunnlag() {
        String fnr = "fnrValue";
        Integer fom = 1988;
        Integer tom = 2018;

        when(restTemplateMock.exchange(urlCaptor.capture(), httpMethodCaptor.capture(), any(), eq(HentOpptjeningsGrunnlagResponse.class))).thenReturn(new ResponseEntity<>(HttpStatus.OK));

        consumer.getInntektListeFromOpptjeningsgrunnlag(fnr, fom, tom);

        assertThat(httpMethodCaptor.getValue(), is(HttpMethod.GET));
        assertThat(urlCaptor.getValue(), is(endpoint + "/opptjeningsgrunnlag/" + fnr + "?fomAr=" + fom + "&tomAr=" + tom));
    }

    @Test
    public void should_return_FailedCallingExternalServiceException_when_401() {
        when(restTemplateMock.exchange("http://poppEndpoint.test/opptjeningsgrunnlag/?fomAr=0&tomAr=0",
                HttpMethod.GET,
                null,
                HentOpptjeningsGrunnlagResponse.class)).thenThrow(new RestClientResponseException("", HttpStatus.UNAUTHORIZED.value(), "", null, null, null));

        FailedCallingExternalServiceException thrown = assertThrows(
                FailedCallingExternalServiceException.class,
                () -> consumer.getInntektListeFromOpptjeningsgrunnlag("", 0, 0));

        assertThat(thrown.getMessage(), is("Error when calling the external service " + CONSUMED_SERVICE + " in " + POPP + ". Received 401 UNAUTHORIZED"));
    }

    @Test
    public void should_return_FailedCallingExternalServiceException_when_512() {
        when(restTemplateMock.exchange("http://poppEndpoint.test/opptjeningsgrunnlag/?fomAr=0&tomAr=0",
                HttpMethod.GET,
                null,
                HentOpptjeningsGrunnlagResponse.class)).thenThrow(new RestClientResponseException("PersonDoesNotExistExceptionDto", 512, "", null, null, null));

        FailedCallingExternalServiceException thrown = assertThrows(
                FailedCallingExternalServiceException.class,
                () -> consumer.getInntektListeFromOpptjeningsgrunnlag("", 0, 0));

        assertThat(thrown.getMessage(), is("Error when calling the external service " + CONSUMED_SERVICE + " in " + POPP + ". Person ikke funnet"));
    }

    @Test
    public void should_return_FailedCallingExternalServiceException_when_500() {
        when(restTemplateMock.exchange("http://poppEndpoint.test/opptjeningsgrunnlag/?fomAr=0&tomAr=0",
                HttpMethod.GET,
                null,
                HentOpptjeningsGrunnlagResponse.class)).thenThrow(new RestClientResponseException("", HttpStatus.INTERNAL_SERVER_ERROR.value(), "", null, null, null));

        FailedCallingExternalServiceException thrown = assertThrows(
                FailedCallingExternalServiceException.class,
                () -> consumer.getInntektListeFromOpptjeningsgrunnlag("", 0, 0));

        assertThat(thrown.getMessage(),
                is("Error when calling the external service " + CONSUMED_SERVICE + " in " + POPP
                        + ". An error occurred in the provider, received 500 INTERNAL SERVER ERROR"));
    }

    @Test
    public void should_return_FailedCallingExternalServiceException_when_RuntimeException() {
        when(restTemplateMock.exchange("http://poppEndpoint.test/opptjeningsgrunnlag/?fomAr=0&tomAr=0",
                HttpMethod.GET,
                null,
                HentOpptjeningsGrunnlagResponse.class)).thenThrow(new RuntimeException());

        FailedCallingExternalServiceException thrown = assertThrows(
                FailedCallingExternalServiceException.class,
                () -> consumer.getInntektListeFromOpptjeningsgrunnlag("", 0, 0));

        assertThat(thrown.getMessage(), is("Error when calling the external service " + CONSUMED_SERVICE + " in " + POPP + ". An error occurred in the consumer"));
    }
}