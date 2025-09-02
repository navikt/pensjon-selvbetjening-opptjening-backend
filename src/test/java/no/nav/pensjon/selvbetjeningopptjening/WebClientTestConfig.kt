package no.nav.pensjon.selvbetjeningopptjening

import no.nav.pensjon.selvbetjeningopptjening.tech.web.WebClientConfig
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.web.reactive.function.client.WebClient
import kotlin.also

@TestConfiguration
open class WebClientTestConfig {

    @Bean
    open fun webClientBuilder(): WebClient.Builder =
        WebClient.builder().also { WebClientConfig().customize(it) }
}
