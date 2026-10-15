package no.nav.pensjon.selvbetjeningopptjening.consumer.restpensjon.acl

/**
 * Korresponderer med HentRestpensjonerResponse i POPP
 */
data class PoppRestpensjonResult(
    val restpensjoner: List<PoppRestpensjon>? = null
)