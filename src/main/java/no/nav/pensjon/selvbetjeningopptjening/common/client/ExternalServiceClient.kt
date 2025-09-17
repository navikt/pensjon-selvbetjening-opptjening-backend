package no.nav.pensjon.selvbetjeningopptjening.common.client

import mu.KotlinLogging
import no.nav.pensjon.selvbetjeningopptjening.tech.metric.MetricResult.BAD_CLIENT
import no.nav.pensjon.selvbetjeningopptjening.tech.metric.MetricResult.BAD_SERVER
import no.nav.pensjon.selvbetjeningopptjening.tech.metric.Metrics
import no.nav.pensjon.selvbetjeningopptjening.tech.security.egress.config.EgressService
import no.nav.pensjon.selvbetjeningopptjening.tech.web.EgressException
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.client.WebClientRequestException
import reactor.util.retry.Retry
import reactor.util.retry.RetryBackoffSpec
import java.time.Duration
import kotlin.also
import kotlin.text.toLong

abstract class ExternalServiceClient(private val retryAttempts: String) {

    abstract fun toString(e: EgressException, uri: String): String

    abstract fun service(): EgressService

    private val log = KotlinLogging.logger {}

    protected fun retryBackoffSpec(uri: String): RetryBackoffSpec =
        Retry.backoff(retryAttempts.toLong(), Duration.ofSeconds(1))
            .filter { it is EgressException && !it.isClientError }
            .onRetryExhaustedThrow { backoff, signal -> handleFailure(backoff, signal, uri) }

    private fun handleFailure(backoff: RetryBackoffSpec, retrySignal: Retry.RetrySignal, uri: String): Throwable {
        log.info { "Retried calling $uri ${backoff.maxAttempts} times" }

        return when (val failure = retrySignal.failure()) {
            is WebClientRequestException -> EgressException(
                message = "Failed calling ${failure.uri}",
                cause = failure,
                statusCode = HttpStatus.BAD_REQUEST
            ).also { countCalls(BAD_CLIENT) }

            is EgressException -> EgressException(
                message = toString(failure, uri),
                cause = failure,
                statusCode = failure.statusCode
            ).also { countCalls(metricResult(failure)) }

            else -> failure
        }
    }

    protected fun countCalls(result: String) {
        Metrics.countEgressCall(service().shortName, result)
    }

    private fun metricResult(failure: EgressException) = if (failure.isClientError) BAD_CLIENT else BAD_SERVER
}
