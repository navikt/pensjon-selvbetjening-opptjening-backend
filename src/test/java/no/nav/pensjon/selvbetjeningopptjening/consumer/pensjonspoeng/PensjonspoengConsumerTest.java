package no.nav.pensjon.selvbetjeningopptjening.consumer.pensjonspoeng;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import static no.nav.pensjon.selvbetjeningopptjening.consumer.pensjonspoeng.PensjonspoengConsumer.CONSUMED_SERVICE;
import static no.nav.pensjon.selvbetjeningopptjening.util.Constants.POPP;

import java.util.List;

import no.nav.pensjon.selvbetjeningopptjening.opptjening.Pensjonspoeng;
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
import no.nav.pensjon.selvbetjeningopptjening.model.PensjonspoengDto;

@ExtendWith(MockitoExtension.class)
class PensjonspoengConsumerTest {
    private final String endpoint = "http://poppEndpoint.test";

    @Mock
    private RestTemplate restTemplateMock;

    private PensjonspoengConsumer consumer;

    @Captor
    private ArgumentCaptor<String> urlCaptor;

    @Captor
    private ArgumentCaptor<HttpMethod> httpMethodCaptor;

    @BeforeEach
    void setup() {
        consumer = new PensjonspoengConsumer(endpoint);
        consumer.setRestTemplate(restTemplateMock);
    }

    @Test
    void should_return_list_of_Pensjonspoeng_when_getPensjonspoengListe() {
        PensjonspoengListeResponse response = new PensjonspoengListeResponse();
        response.setPensjonspoeng(List.of(pensjonspoengDto()));
        ResponseEntity<PensjonspoengListeResponse> entity = new ResponseEntity<>(response, null, HttpStatus.OK);
        when(restTemplateMock.exchange(urlCaptor.capture(), any(), any(), eq(PensjonspoengListeResponse.class))).thenReturn(entity);

        List<Pensjonspoeng> pensjonspoengList = consumer.getPensjonspoengListe("fnr");

        assertEquals(1, pensjonspoengList.size());
        assertEquals(1991, pensjonspoengList.get(0).getYear());
    }

    @Test
    void should_add_fnr_as_pathrparam_when_GET_getPensjonspoengListe() {
        String expectedFnr = "fnrValue";

        when(restTemplateMock.exchange(
                urlCaptor.capture(),
                httpMethodCaptor.capture(),
                eq(null),
                eq(PensjonspoengListeResponse.class))).thenReturn(new ResponseEntity<>(HttpStatus.OK));

        consumer.getPensjonspoengListe(expectedFnr);

        assertThat(httpMethodCaptor.getValue(), is(HttpMethod.GET));
        assertThat(urlCaptor.getValue(), is(endpoint + "/pensjonspoeng/" + expectedFnr));
    }

    @Test
    void should_return_FailedCallingExternalServiceException_when_401() {
        when(restTemplateMock.exchange("http://poppEndpoint.test/pensjonspoeng/",
                HttpMethod.GET,
                null,
                PensjonspoengListeResponse.class)).thenThrow(new RestClientResponseException("", HttpStatus.UNAUTHORIZED.value(), "", null, null, null));

        FailedCallingExternalServiceException thrown = assertThrows(
                FailedCallingExternalServiceException.class,
                () -> consumer.getPensjonspoengListe(""));

        assertThat(thrown.getMessage(), is("Error when calling the external service " + CONSUMED_SERVICE + " in " + POPP + ". Received 401 UNAUTHORIZED"));
    }

    @Test
    void should_return_FailedCallingExternalServiceException_when_512() {
        when(restTemplateMock.exchange("http://poppEndpoint.test/pensjonspoeng/",
                HttpMethod.GET,
                null,
                PensjonspoengListeResponse.class)).thenThrow(new RestClientResponseException("PersonDoesNotExistExceptionDto", 512, "", null, null, null));

        FailedCallingExternalServiceException thrown = assertThrows(
                FailedCallingExternalServiceException.class,
                () -> consumer.getPensjonspoengListe(""));

        assertThat(thrown.getMessage(), is("Error when calling the external service " + CONSUMED_SERVICE + " in " + POPP + ". Person ikke funnet"));
    }

    @Test
    void should_return_FailedCallingExternalServiceException_when_500() {
        when(restTemplateMock.exchange("http://poppEndpoint.test/pensjonspoeng/",
                HttpMethod.GET,
                null,
                PensjonspoengListeResponse.class)).thenThrow(new RestClientResponseException("", HttpStatus.INTERNAL_SERVER_ERROR.value(), "", null, null, null));

        FailedCallingExternalServiceException thrown = assertThrows(
                FailedCallingExternalServiceException.class,
                () -> consumer.getPensjonspoengListe(""));

        assertThat(thrown.getMessage(),
                is("Error when calling the external service " + CONSUMED_SERVICE + " in " + POPP + ". An error occurred in the provider, received 500 INTERNAL SERVER "
                        + "ERROR"));
    }

    @Test
    void should_return_FailedCallingExternalServiceException_when_RuntimeException() {
        when(restTemplateMock.exchange("http://poppEndpoint.test/pensjonspoeng/",
                HttpMethod.GET,
                null,
                PensjonspoengListeResponse.class)).thenThrow(new RuntimeException());

        FailedCallingExternalServiceException thrown = assertThrows(
                FailedCallingExternalServiceException.class,
                () -> consumer.getPensjonspoengListe(""));

        assertThat(thrown.getMessage(), is("Error when calling the external service " + CONSUMED_SERVICE + " in " + POPP + ". An error occurred in the consumer"));
    }

    private static PensjonspoengDto pensjonspoengDto() {
        var dto = new PensjonspoengDto();
        dto.setAr(1991);
        return dto;
    }
}
