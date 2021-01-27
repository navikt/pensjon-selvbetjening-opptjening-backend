package no.nav.pensjon.selvbetjeningopptjening.consumer;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PoppUtilTest {

    @Test
    void test_unauthorized() {
        testException("Error when calling the external service service in POPP. Received 401 UNAUTHORIZED",
                "message",
                401);
    }

    @Test
    void test_checkedException_httpStatus() {
        testException("Error when calling the external service service in POPP. Person ikke funnet",
                "foo PersonDoesNotExistExceptionDto bar",
                512);
    }

    @Test
    void test_internalServerError() {
        testException("Error when calling the external service service in POPP. An error occurred in the provider, received 500 INTERNAL SERVER ERROR",
                "message",
                500);
    }

    @Test
    void test_unhandledStatus() {
        testException("Error when calling the external service service in POPP. An error occurred in the provider",
                "message",
                418);
    }

    @Test
    void handle_WebClientResponseException_unauthorized() {
        testWebClientResponseException(
                "Error when calling the external service service in POPP. Received 401 UNAUTHORIZED",
                "body",
                401);
    }

    @Test
    void handle_WebClientResponseException_checkedException_httpStatus() {
        testWebClientResponseException(
                "Error when calling the external service service in POPP. Person ikke funnet",
                "foo PersonDoesNotExistExceptionDto bar",
                512);
    }

    @Test
    void handle_WebClientResponseException_internalServerError() {
        testWebClientResponseException(
                "Error when calling the external service service in POPP. An error occurred in the provider," +
                        " received 500 INTERNAL SERVER ERROR",
                "body",
                500);
    }

    @Test
    void handle_WebClientResponseException_unhandledStatus() {
        testWebClientResponseException(
                "Error when calling the external service service in POPP. An error occurred in the provider",
                "body",
                418);
    }

    @Test
    void handle_RuntimeException_returns_FailedCallingExternalServiceException() {
        var exception = new RuntimeException("oops");

        FailedCallingExternalServiceException actual = PoppUtil.handle(exception, "service");

        assertEquals("Error when calling the external service service in POPP. Failed to call service", actual.getMessage());
        assertEquals("oops", actual.getCause().getMessage());
    }

    private static void testException(String expectedMessage, String message, int status) {
        var exception = new RestClientResponseException(message, status, "status", new HttpHeaders(), "body".getBytes(), StandardCharsets.UTF_8);
        FailedCallingExternalServiceException actual = PoppUtil.handle(exception, "service");
        assertEquals(expectedMessage, actual.getMessage());
    }

    private static void testWebClientResponseException(String expectedMessage, String body, int status) {
        var exception = new WebClientResponseException("message", status, "status", new HttpHeaders(), body.getBytes(), StandardCharsets.UTF_8);
        FailedCallingExternalServiceException actual = PoppUtil.handle(exception, "service");
        assertEquals(expectedMessage, actual.getMessage());
    }
}
