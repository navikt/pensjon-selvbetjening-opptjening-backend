package no.nav.pensjon.selvbetjeningopptjening.tech.web

import io.netty.channel.ChannelOption
import io.netty.handler.logging.LogLevel
import io.netty.handler.timeout.ReadTimeoutHandler
import io.netty.handler.timeout.WriteTimeoutHandler
import org.springframework.boot.web.reactive.function.client.WebClientCustomizer
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import org.springframework.web.reactive.function.client.ExchangeStrategies
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import reactor.netty.Connection
import reactor.netty.http.client.HttpClient
import reactor.netty.resources.ConnectionProvider
import reactor.netty.transport.logging.AdvancedByteBufFormat
import java.time.Duration
import java.util.concurrent.TimeUnit
import kotlin.collections.firstOrNull

@Configuration
open class WebClientConfig : WebClientCustomizer {

    override fun customize(webClientBuilder: WebClient.Builder) {
        webClientBuilder
            .clientConnector(
                ReactorClientHttpConnector(
                    HttpClient.create(connectionProvider())
                        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, TIMEOUT.toInt())
                        .responseTimeout(Duration.ofMillis(TIMEOUT))
                        .doOnConnected(::addTimeoutHandlers)
                        .wiretap(
                            "reactor.netty.http.client.HttpClient",
                            LogLevel.DEBUG,
                            AdvancedByteBufFormat.TEXTUAL
                        )
                )
            )
            .exchangeStrategies(largeBufferStrategies())
            .filter(filterResponse())
    }

    companion object {
        private const val MAX_IN_MEMORY_SIZE = 10485760 // 10 MB (10 * 1024 * 1024)
        private const val TIMEOUT: Long = 20_000

        private fun addTimeoutHandlers(connection: Connection) {
            connection.addHandlerLast(ReadTimeoutHandler(TIMEOUT, TimeUnit.MILLISECONDS))
            connection.addHandlerLast(WriteTimeoutHandler(TIMEOUT, TimeUnit.MILLISECONDS))
        }

        /**
         * From https://github.com/reactor/reactor-netty/issues/764:
         * The idle timeout varies depending on provider, so it's hard to have a good default for this without
         * making it overly aggressive i.e. setting it < 60 seconds. If too aggressive then new
         * connections are established potentially more than needed, i.e. more SSL handshakes etc.
         * -----
         * Values used below are taken from example in:
         * https://projectreactor.io/docs/netty/release/reference/index.html#connection-pool-timeout
         */
        private fun connectionProvider() =
            ConnectionProvider.builder("custom")
                .maxConnections(50)
                .maxIdleTime(Duration.ofSeconds(20)) // too aggressive? (see Javadoc comment above)
                .maxLifeTime(Duration.ofSeconds(60))
                .pendingAcquireTimeout(Duration.ofSeconds(60))
                .evictInBackground(Duration.ofSeconds(120))
                .metrics(true)
                .build()

        private fun largeBufferStrategies(): ExchangeStrategies =
            ExchangeStrategies.builder()
                .codecs { it.defaultCodecs().maxInMemorySize(MAX_IN_MEMORY_SIZE) }
                .build()

        private fun filterResponse() = ExchangeFilterFunction.ofResponseProcessor(::filter)

        private fun filter(response: ClientResponse): Mono<ClientResponse> {
            val statusCode = response.statusCode()

            return if (statusCode.is4xxClientError)
                clientError(statusCode, response)
            else if (statusCode.is5xxServerError)
                serverError(response)
            else
                Mono.just(response)
        }

        private fun clientError(statusCode: HttpStatusCode, response: ClientResponse): Mono<ClientResponse> =
            response
                .bodyToMono(String::class.java)
                .defaultIfEmpty(emptyResponseInfo(response))
                .flatMap { Mono.error(EgressException(message = it, statusCode = statusCode)) }

        private fun serverError(response: ClientResponse): Mono<ClientResponse> =
            response
                .bodyToMono(String::class.java)
                .flatMap { Mono.error(EgressException(message = it)) }

        private fun emptyResponseInfo(response: ClientResponse): String {
            val statusCode = response.statusCode()

            return if (isAccessDenied(statusCode))
                "$statusCode: ${reasonForAccessDenial(response)}"
            else
                statusCode.toString()
        }

        private fun isAccessDenied(statusCode: HttpStatusCode) =
            statusCode == HttpStatus.FORBIDDEN || statusCode == HttpStatus.UNAUTHORIZED

        private fun reasonForAccessDenial(response: ClientResponse): String =
            response.headers().asHttpHeaders()[HttpHeaders.WWW_AUTHENTICATE]?.firstOrNull() ?: "(access denied)"
    }
}
