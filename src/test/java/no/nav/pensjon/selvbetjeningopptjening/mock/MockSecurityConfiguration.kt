package no.nav.pensjon.selvbetjeningopptjening.mock

import no.nav.pensjon.selvbetjeningopptjening.opptjening.Pid
import org.springframework.boot.test.context.TestComponent

@TestComponent
class MockSecurityConfiguration {

    companion object {
        val pid = Pid("12906498357") // synthetic fødselsnummer
    }
}
