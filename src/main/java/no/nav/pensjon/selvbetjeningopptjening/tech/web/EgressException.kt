package no.nav.pensjon.selvbetjeningopptjening.tech.web

import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode

class EgressException(
    message: String,
    cause: Throwable? = null,
    val statusCode: HttpStatusCode? = null
) : RuntimeException(message, cause) {
    val isClientError: Boolean = statusCode?.is4xxClientError ?: false
    val isConflict: Boolean = statusCode == HttpStatus.CONFLICT
}
