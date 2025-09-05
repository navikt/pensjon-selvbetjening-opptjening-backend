package no.nav.pensjon.selvbetjeningopptjening.tech.security.ingress.impersonal.skjerming

import no.nav.pensjon.selvbetjeningopptjening.opptjening.Pid
import no.nav.pensjon.selvbetjeningopptjening.tech.security.ingress.impersonal.skjerming.client.SkjermingClient
import org.springframework.stereotype.Service

@Service
class SkjermingService(private val client: SkjermingClient) {

    fun personErTilgjengelig(pid: Pid): Boolean = client.personErTilgjengelig(pid)
}
