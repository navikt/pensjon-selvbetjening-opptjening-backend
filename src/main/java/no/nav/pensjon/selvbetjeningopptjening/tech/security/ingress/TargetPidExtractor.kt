package no.nav.pensjon.selvbetjeningopptjening.tech.security.ingress

import mu.KotlinLogging
import no.nav.pensjon.selvbetjeningopptjening.opptjening.Pid
import no.nav.pensjon.selvbetjeningopptjening.opptjening.PidValidationException
import no.nav.pensjon.selvbetjeningopptjening.tech.security.egress.EnrichedAuthentication
import no.nav.pensjon.selvbetjeningopptjening.tech.security.egress.enriched
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import kotlin.let

/**
 * Extracts the person-ID (PID) of the 'target person' from the security context.
 * The 'target person' is the person whose data is to be processed in the request.
 * In an 'on behalf of' context the 'target person' is different from logged-in person,
 * e.g., when the logged-in person is a 'fullmektig' or a 'veileder'.
 */
@Component
class TargetPidExtractor {

    private val log = KotlinLogging.logger {}

    fun pid(): Pid =
        authentication()?.targetPid() ?: missingPid()

    private fun missingPid(): Pid {
        "No PID found".let {
            log.warn { it }
            throw PidValidationException(it)
        }
    }

    private companion object {
        private fun authentication(): EnrichedAuthentication? =
            SecurityContextHolder.getContext().authentication?.enriched()
    }
}
