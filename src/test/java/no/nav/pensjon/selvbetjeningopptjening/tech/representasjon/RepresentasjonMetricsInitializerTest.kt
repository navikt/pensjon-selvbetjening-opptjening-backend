package no.nav.pensjon.selvbetjeningopptjening.tech.representasjon

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.doubles.shouldBeExactly
import io.kotest.matchers.shouldNotBe
import io.micrometer.core.instrument.simple.SimpleMeterRegistry

class RepresentasjonMetricsInitializerTest : FunSpec({

    lateinit var meterRegistry: SimpleMeterRegistry

    beforeEach {
        meterRegistry = SimpleMeterRegistry()
        io.micrometer.core.instrument.Metrics.globalRegistry.add(meterRegistry)
    }

    afterEach {
        io.micrometer.core.instrument.Metrics.globalRegistry.remove(meterRegistry)
        meterRegistry.clear()
        meterRegistry.close()
    }

    test("pre-registers all reachable OBO outcomes for GET") {
        RepresentasjonMetricsInitializer().registerCounters()

        OboTilgangOutcome.entries.forEach { outcome ->
            val counter = io.micrometer.core.instrument.Metrics.globalRegistry
                .find(REPRESENTASJON_OBO_TILGANG_EVENT)
                .tags("outcome", outcome.tag, "method", "GET")
                .counter()

            counter shouldNotBe null
            counter!!.count() shouldBeExactly 0.0
        }
    }
})
