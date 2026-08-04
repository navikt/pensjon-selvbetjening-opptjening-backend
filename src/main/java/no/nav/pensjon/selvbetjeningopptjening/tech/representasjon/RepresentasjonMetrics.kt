package no.nav.pensjon.selvbetjeningopptjening.tech.representasjon

import io.micrometer.core.instrument.Metrics
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

const val REPRESENTASJON_OBO_TILGANG_EVENT = "representasjon_obo_tilgang"
const val EVENT_OBO_TILGANG_AVVIST = "obo_tilgang_avvist"

enum class OboTilgangOutcome(val tag: String) {
    INNVILGET("innvilget"),
    INGEN_GYLDIG_REPRESENTASJON("ingen_gyldig_representasjon"),
    FULLMAKT_FEIL("fullmakt_feil")
}

fun countOboTilgang(outcome: OboTilgangOutcome, method: String) {
    Metrics.counter(REPRESENTASJON_OBO_TILGANG_EVENT, "outcome", outcome.tag, "method", method).increment()
}

@Component
class RepresentasjonMetricsInitializer {

    @EventListener(ApplicationReadyEvent::class)
    fun registerCounters() {
        for (outcome in OboTilgangOutcome.entries) {
            for (method in OBO_HTTP_METHODS) {
                Metrics.counter(REPRESENTASJON_OBO_TILGANG_EVENT, "outcome", outcome.tag, "method", method)
            }
        }
    }

    companion object {
        private val OBO_HTTP_METHODS = listOf("GET")
    }
}
