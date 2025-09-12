package no.nav.pensjon.selvbetjeningopptjening.tech.security.egress.token.validation

import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class NowProvider : TimeProvider {
    override fun time(): LocalDateTime = LocalDateTime.now()
}
