package no.nav.pensjon.selvbetjeningopptjening.tech.trace

import org.slf4j.MDC
import org.springframework.stereotype.Component
import kotlin.also

@Component
class TraceAid(private val callIdGenerator: CallIdGenerator) {

    fun begin() {
        MDC.put(CALL_ID_KEY, callIdGenerator.newId())
    }

    fun callId(): String =
        MDC.get(CALL_ID_KEY) ?: callIdGenerator.newId().also { MDC.put(CALL_ID_KEY, it) }

    fun end() {
        MDC.clear()
    }

    private companion object {
        private const val CALL_ID_KEY = "Nav-Call-Id"
    }
}
