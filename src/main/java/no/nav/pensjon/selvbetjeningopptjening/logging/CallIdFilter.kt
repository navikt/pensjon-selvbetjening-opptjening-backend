package no.nav.pensjon.selvbetjeningopptjening.logging

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpFilter
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.MDC
import org.springframework.stereotype.Component
import java.util.*

@Component
class CallIdFilter : HttpFilter() {

    override fun doFilter(
        request: HttpServletRequest?,
        response: HttpServletResponse?,
        filterChain: FilterChain
    ) {
        try {
            MDC.put(CALL_ID_KEY, callId())
            filterChain.doFilter(request, response)
        } finally {
            MDC.remove(CALL_ID_KEY)
        }
    }

    private fun callId(): String =
        UUID.randomUUID().toString()
}

const val CALL_ID_KEY = "Nav-Call-Id"
