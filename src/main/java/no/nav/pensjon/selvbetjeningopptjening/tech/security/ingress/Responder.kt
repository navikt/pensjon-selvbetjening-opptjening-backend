package no.nav.pensjon.selvbetjeningopptjening.tech.security.ingress

import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType

object Responder {
    fun respondForbidden(response: HttpServletResponse, reason: String) {
        response.status = HttpStatus.FORBIDDEN.value()
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.writer.append("""{ "reason": "$reason" }""")
    }
}
