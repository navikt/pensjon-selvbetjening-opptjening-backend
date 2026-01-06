package no.nav.pensjon.selvbetjeningopptjening.opptjening.client.popp

import no.nav.pensjon.selvbetjeningopptjening.consumer.FailedCallingExternalServiceException
import org.springframework.http.HttpStatus
import org.springframework.web.client.RestClientResponseException
import org.springframework.web.reactive.function.client.WebClientResponseException

object ErrorHandler {

    private const val CHECKED_EXCEPTION_HTTP_STATUS: Int = 512
    private const val PROVIDER: String = "POPP"

    fun handle(e: RestClientResponseException, service: String): FailedCallingExternalServiceException {
        val status = e.statusCode

        return when {
            status === HttpStatus.UNAUTHORIZED -> FailedCallingExternalServiceException(
                PROVIDER,
                service,
                "Received 401 UNAUTHORIZED",
                e
            )

            status.value() == CHECKED_EXCEPTION_HTTP_STATUS && isPersonDoesNotExistMessage(e.message) -> FailedCallingExternalServiceException(
                PROVIDER,
                service,
                "Person ikke funnet",
                e
            )

            status === HttpStatus.INTERNAL_SERVER_ERROR -> FailedCallingExternalServiceException(
                PROVIDER,
                service,
                "An error occurred in the provider, received 500 INTERNAL SERVER ERROR",
                e
            )

            else -> FailedCallingExternalServiceException(PROVIDER, service, "An error occurred in the provider", e)
        }
    }

    fun handle(e: WebClientResponseException, service: String): FailedCallingExternalServiceException {
        val status = e.statusCode

        return when {
            status === HttpStatus.UNAUTHORIZED -> FailedCallingExternalServiceException(
                PROVIDER,
                service,
                "Received 401 UNAUTHORIZED",
                e
            )

            status.value() == CHECKED_EXCEPTION_HTTP_STATUS && isPersonDoesNotExistMessage(e.responseBodyAsString) ->
                FailedCallingExternalServiceException(PROVIDER, service, "Person ikke funnet", e)

            status === HttpStatus.INTERNAL_SERVER_ERROR ->
                FailedCallingExternalServiceException(
                    PROVIDER,
                    service,
                    "An error occurred in the provider, received 500 INTERNAL SERVER ERROR",
                    e
                )

            else -> FailedCallingExternalServiceException(PROVIDER, service, "An error occurred in the provider", e)
        }
    }

    fun handle(e: RuntimeException?, service: String) =
        FailedCallingExternalServiceException(PROVIDER, service, "Failed to call service", e)

    private fun isPersonDoesNotExistMessage(responseBody: String?): Boolean =
        responseBody?.contains("PersonDoesNotExistExceptionDto") == true
}