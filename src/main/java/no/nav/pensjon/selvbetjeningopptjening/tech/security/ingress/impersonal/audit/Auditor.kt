package no.nav.pensjon.selvbetjeningopptjening.tech.security.ingress.impersonal.audit

import mu.KotlinLogging
import no.nav.pensjon.selvbetjeningopptjening.opptjening.Pid
import org.slf4j.event.Level
import org.springframework.stereotype.Component
import java.time.ZonedDateTime

/**
 * Sends info to the auditing system when a user (Nav employee)
 * performs some task on behalf of another person.
 * The info is formatted according to ArcSight CEF (Common Event Format).
 */
@Component
class Auditor(private val ansattIdExtractor: SecurityContextNavIdExtractor) {

    private val log = KotlinLogging.logger("AUDIT_LOGGER")

    fun audit(onBehalfOfPid: Pid, requestUri: String) {
        log.info { cefEntry(ansattIdExtractor.id(), onBehalfOfPid, requestUri).format() }
    }

    private fun cefEntry(ansattId: String, onBehalfOfPid: Pid, requestUri: String) =
        CefEntry(
            timestamp = now(),
            level = Level.INFO,
            deviceEventClassId = DEVICE_EVENT_CLASS_ID,
            name = "Datahenting paa vegne av",
            message = "$USER_TYPE kaller $requestUri",
            sourceUserId = ansattId,
            destinationUserId = onBehalfOfPid.pid
        )

    private companion object {
        private const val DEVICE_EVENT_CLASS_ID = "audit:read"
        private const val USER_TYPE = "Nav-ansatt"

        private fun now() = ZonedDateTime.now().toInstant().toEpochMilli()
    }
}
