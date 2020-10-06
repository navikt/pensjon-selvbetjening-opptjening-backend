package no.nav.pensjon.selvbetjeningopptjening.consumer;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestClientResponseException;

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

    private static void testException(String expectedMessage, String message, int status) {
        var exception = new RestClientResponseException(message, status, "status", new HttpHeaders(), "body".getBytes(), StandardCharsets.UTF_8);
        FailedCallingExternalServiceException actual = PoppUtil.handle(exception, "service");
        assertEquals(expectedMessage, actual.getMessage());
    }
}
