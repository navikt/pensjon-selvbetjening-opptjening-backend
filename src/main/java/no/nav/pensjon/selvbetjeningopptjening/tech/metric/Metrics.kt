package no.nav.pensjon.selvbetjeningopptjening.tech.metric

import io.micrometer.core.instrument.Metrics

object Metrics {
    private const val PREFIX = "sob" // selvbetjening-opptjening-backend

    fun countEgressCall(service: String, result: String) {
        Metrics
            .counter("${PREFIX}_egress_call", "service", service, "result", result)
            .increment()
    }

    fun countEvent(eventName: String, result: String) {
        Metrics.counter("${PREFIX}_$eventName", "result", result).increment()
    }

    fun countType(eventName: String, type: String) {
        Metrics.counter("${PREFIX}_$eventName", "type", type).increment()
    }
}

object MetricResult {
    const val BAD_CLIENT = "bad-client"
    const val BAD_SERVER = "bad-server"
    const val BAD_XML = "bad-xml"
    const val OK = "ok"
}
