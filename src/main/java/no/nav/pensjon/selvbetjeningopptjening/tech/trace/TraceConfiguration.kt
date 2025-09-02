package no.nav.pensjon.selvbetjeningopptjening.tech.trace

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.*

@Configuration
open class TraceConfiguration {

    @Bean
    open fun callIdGenerator() = CallIdGenerator { UUID.randomUUID().toString() }
}
