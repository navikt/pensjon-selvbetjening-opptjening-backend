package no.nav.pensjon.selvbetjeningopptjening.consumer.opptjeningsgrunnlag;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import static no.nav.pensjon.selvbetjeningopptjening.util.Constants.POPP;

import java.util.List;

import no.nav.pensjon.selvbetjeningopptjening.opptjening.Inntekt;
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
import no.nav.pensjon.selvbetjeningopptjening.model.InntektDto;
import no.nav.pensjon.selvbetjeningopptjening.model.OpptjeningsGrunnlagDto;

@ExtendWith(MockitoExtension.class)
class OpptjeningsgrunnlagConsumerTest {

    private static final String CONSUMED_SERVICE = "PROPOPP007 hentOpptjeningsgrunnlag";
    private static final String ENDPOINT = "http://poppEndpoint.test";

    @Mock
    private RestTemplate restTemplateMock;

    private OpptjeningsgrunnlagConsumer consumer;

    @Captor
    private ArgumentCaptor<String> urlCaptor;

    @Captor
    private ArgumentCaptor<HttpMethod> httpMethodCaptor;

    @BeforeEach
    void setup() {
        consumer = new OpptjeningsgrunnlagConsumer(ENDPOINT);
        consumer.setRestTemplate(restTemplateMock);
    }

    @Test
    void should_return_list_of_Inntekt_when_getInntektListeFromOpptjeningsgrunnlag() {
        var response = new HentOpptjeningsGrunnlagResponse();
        var grunnlag = new OpptjeningsGrunnlagDto();
        grunnlag.setInntektListe(List.of(inntektDto()));
        response.setOpptjeningsGrunnlag(grunnlag);
        ResponseEntity<HentOpptjeningsGrunnlagResponse> entity = new ResponseEntity<>(response, null, HttpStatus.OK);
        when(restTemplateMock.exchange(urlCaptor.capture(), any(), any(), eq(HentOpptjeningsGrunnlagResponse.class))).thenReturn(entity);

        List<Inntekt> inntekter = consumer.getInntektListeFromOpptjeningsgrunnlag("fnr", 0, 0);

        assertEquals(1, inntekter.size());
        assertEquals(1991, inntekter.get(0).getYear());
    }

    @Test
    void should_add_fnr_as_pathparam_and_fom_and_tom_as_queryparams_when_GET_getInntektListeFromOpptjeningsgrunnlag() {
        String fnr = "fnrValue";
        Integer fom = 1988;
        Integer tom = 2018;
        when(restTemplateMock.exchange(urlCaptor.capture(), httpMethodCaptor.capture(), any(), eq(HentOpptjeningsGrunnlagResponse.class))).thenReturn(new ResponseEntity<>(HttpStatus.OK));

        consumer.getInntektListeFromOpptjeningsgrunnlag(fnr, fom, tom);

        assertThat(httpMethodCaptor.getValue(), is(HttpMethod.GET));
        assertThat(urlCaptor.getValue(), is(ENDPOINT + "/opptjeningsgrunnlag/" + fnr + "?fomAr=" + fom + "&tomAr=" + tom));
    }

    @Test
    void should_return_FailedCallingExternalServiceException_when_401() {
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
    void should_return_FailedCallingExternalServiceException_when_512() {
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
    void should_return_FailedCallingExternalServiceException_when_500() {
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
    void should_return_FailedCallingExternalServiceException_when_RuntimeException() {
        when(restTemplateMock.exchange("http://poppEndpoint.test/opptjeningsgrunnlag/?fomAr=0&tomAr=0",
                HttpMethod.GET,
                null,
                HentOpptjeningsGrunnlagResponse.class)).thenThrow(new RuntimeException());

        FailedCallingExternalServiceException thrown = assertThrows(
                FailedCallingExternalServiceException.class,
                () -> consumer.getInntektListeFromOpptjeningsgrunnlag("", 0, 0));

        assertThat(thrown.getMessage(), is("Error when calling the external service " + CONSUMED_SERVICE + " in " + POPP + ". An error occurred in the consumer"));
    }

    private static InntektDto inntektDto() {
        var dto = new InntektDto();
        dto.setInntektAr(1991);
        return dto;
    }
}
