package no.nav.pensjon.selvbetjeningopptjening.consumer.restpensjon;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import static no.nav.pensjon.selvbetjeningopptjening.util.Constants.POPP;

import java.util.List;

import no.nav.pensjon.selvbetjeningopptjening.opptjening.Restpensjon;
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
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import no.nav.pensjon.selvbetjeningopptjening.consumer.FailedCallingExternalServiceException;
import no.nav.pensjon.selvbetjeningopptjening.model.RestpensjonDto;

@ExtendWith(MockitoExtension.class)
class RestpensjonConsumerTest {

    private static final String ENDPOINT = "http://poppEndpoint.test";
    private static final String CONSUMED_SERVICE = "PROPOPP013 hentRestpensjoner";
    private RestpensjonConsumer consumer;

    @Mock
    private RestTemplate rest;
    @Captor
    private ArgumentCaptor<String> urlCaptor;
    @Captor
    private ArgumentCaptor<HttpMethod> httpMethodCaptor;

    @BeforeEach
    void setup() {
        consumer = new RestpensjonConsumer(ENDPOINT);
        consumer.setRestTemplate(rest);
    }

    @Test
    void should_return_list_of_Restpensjons_when_getRestpensjonListe() {
        var response = new RestpensjonListeResponse();
        var dto = new RestpensjonDto();
        dto.setRestPensjonstillegg(1.23D);
        response.setRestpensjoner(List.of(dto));
        ResponseEntity<RestpensjonListeResponse> entity = new ResponseEntity<>(response, null, HttpStatus.OK);
        when(rest.exchange(urlCaptor.capture(), any(), any(), eq(RestpensjonListeResponse.class))).thenReturn(entity);

        List<Restpensjon> restpensjoner = consumer.getRestpensjonListe("fnr");

        assertEquals(1, restpensjoner.size());
        assertEquals(1.23D, restpensjoner.get(0).getRestPensjonstillegg());
    }

    @Test
    void should_add_fnr_as_queryParam_when_GET_getRestpensjonListe() {
        String expectedFnr = "fnrValue";
        when(rest.exchange(
                urlCaptor.capture(),
                httpMethodCaptor.capture(),
                eq(null),
                eq(RestpensjonListeResponse.class))).thenReturn(new ResponseEntity<>(HttpStatus.OK));

        consumer.getRestpensjonListe(expectedFnr);

        assertThat(httpMethodCaptor.getValue(), is(HttpMethod.GET));
        assertThat(urlCaptor.getValue(), is(ENDPOINT + "/restpensjon/" + expectedFnr + "?hentSiste=false"));
    }

    @Test
    void should_return_FailedCallingExternalServiceException_when_401() {
        when(rest.exchange("http://poppEndpoint.test/restpensjon/?hentSiste=false",
                HttpMethod.GET,
                null,
                RestpensjonListeResponse.class)).thenThrow(new RestClientResponseException("", HttpStatus.UNAUTHORIZED.value(), "", null, null, null));

        var thrown = assertThrows(
                FailedCallingExternalServiceException.class,
                () -> consumer.getRestpensjonListe(""));

        assertThat(thrown.getMessage(), is("Error when calling the external service " + CONSUMED_SERVICE + " in " + POPP + ". Received 401 UNAUTHORIZED"));
    }

    @Test
    void should_return_FailedCallingExternalServiceException_when_512() {
        when(rest.exchange("http://poppEndpoint.test/restpensjon/?hentSiste=false",
                HttpMethod.GET,
                null,
                RestpensjonListeResponse.class)).thenThrow(new RestClientResponseException("PersonDoesNotExistExceptionDto", 512, "", null, null, null));

        var thrown = assertThrows(
                FailedCallingExternalServiceException.class,
                () -> consumer.getRestpensjonListe(""));

        assertThat(thrown.getMessage(), is("Error when calling the external service " + CONSUMED_SERVICE + " in " + POPP + ". Person ikke funnet"));
    }

    @Test
    void should_return_FailedCallingExternalServiceException_when_500() {
        when(rest.exchange("http://poppEndpoint.test/restpensjon/?hentSiste=false",
                HttpMethod.GET,
                null,
                RestpensjonListeResponse.class)).thenThrow(new RestClientResponseException("", HttpStatus.INTERNAL_SERVER_ERROR.value(), "", null, null, null));

        var thrown = assertThrows(
                FailedCallingExternalServiceException.class,
                () -> consumer.getRestpensjonListe(""));

        assertThat(thrown.getMessage(),
                is("Error when calling the external service " + CONSUMED_SERVICE + " in " + POPP + ". An error occurred in the provider, received 500 INTERNAL SERVER "
                        + "ERROR"));
    }

    @Test
    void should_return_FailedCallingExternalServiceException_when_RestClientException() {
        when(rest.exchange("http://poppEndpoint.test/restpensjon/?hentSiste=false",
                HttpMethod.GET,
                null,
                RestpensjonListeResponse.class)).thenThrow(new RestClientException("oops"));

        var thrown = assertThrows(
                FailedCallingExternalServiceException.class,
                () -> consumer.getRestpensjonListe(""));

        assertThat(thrown.getMessage(), is("Error when calling the external service " + CONSUMED_SERVICE + " in " + POPP + ". Failed to access service"));
    }
}
