package no.nav.pensjon.selvbetjeningopptjening.opptjening.client.popp

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import no.nav.pensjon.selvbetjeningopptjening.consumer.FailedCallingExternalServiceException
import org.springframework.http.HttpHeaders
import org.springframework.web.client.RestClientResponseException
import org.springframework.web.reactive.function.client.WebClientResponseException
import java.nio.charset.StandardCharsets

class ErrorHandlerTest : ShouldSpec({

    context("RestClientResponseException") {
        should("handle unauthorized") {
            testRestClientResponseException(
                message = "message",
                status = 401,
                expectedMessage = "Error when calling the external service service in POPP. Received 401 UNAUTHORIZED"
            )
        }

        should("handle missing person") {
            testRestClientResponseException(
                message = "foo PersonDoesNotExistExceptionDto bar",
                status = 512,
                expectedMessage = "Error when calling the external service service in POPP. Person ikke funnet"
            )
        }

        should("handle internal server error") {
            testRestClientResponseException(
                message = "message",
                status = 500,
                expectedMessage = "Error when calling the external service service in POPP. An error occurred in the provider, received 500 INTERNAL SERVER ERROR"
            )
        }

        should("handle unexpected HTTP status") {
            testRestClientResponseException(
                message = "message",
                status = 418,
                expectedMessage = "Error when calling the external service service in POPP. An error occurred in the provider"
            )
        }
    }

    context("WebClientResponseException") {
        should("handle unauthorized") {
            testWebClientResponseException(
                body = "body",
                status = 401,
                expectedMessage = "Error when calling the external service service in POPP. Received 401 UNAUTHORIZED"
            )
        }

        should("handle missing person") {
            testWebClientResponseException(
                body = "foo PersonDoesNotExistExceptionDto bar",
                status = 512,
                expectedMessage = "Error when calling the external service service in POPP. Person ikke funnet"
            )
        }

        should("handle internal server error") {
            testWebClientResponseException(
                body = "body",
                status = 500,
                expectedMessage = "Error when calling the external service service in POPP. An error occurred in the provider," +
                        " received 500 INTERNAL SERVER ERROR"
            )
        }

        should("handle unexpected HTTP status") {
            testWebClientResponseException(
                body = "body",
                status = 418,
                expectedMessage = "Error when calling the external service service in POPP. An error occurred in the provider"
            )
        }
    }

    context("other RuntimeException") {
        should("return 'failed calling external service' exception") {
            val exception = RuntimeException("oops")

            val result: FailedCallingExternalServiceException = ErrorHandler.specificException(exception, "service")

            with(result) {
                message shouldBe "Error when calling the external service service in POPP. Failed to call service"
                cause!!.message shouldBe "oops"
            }
        }
    }
})

private fun testRestClientResponseException(message: String, status: Int, expectedMessage: String) {
    val exception = RestClientResponseException(
        message,
        status,
        "status",
        HttpHeaders(),
        "body".toByteArray(),
        StandardCharsets.UTF_8
    )

    ErrorHandler.serviceException(e = exception, service = "service").message shouldBe expectedMessage
}

private fun testWebClientResponseException(body: String, status: Int, expectedMessage: String) {
    val exception = WebClientResponseException(
        "message",
        status,
        "status",
        HttpHeaders(),
        body.toByteArray(),
        StandardCharsets.UTF_8
    )

    ErrorHandler.serviceException(e = exception, service = "service").message shouldBe expectedMessage
}