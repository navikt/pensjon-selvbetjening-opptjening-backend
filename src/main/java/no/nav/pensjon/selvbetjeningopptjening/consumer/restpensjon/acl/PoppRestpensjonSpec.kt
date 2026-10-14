package no.nav.pensjon.selvbetjeningopptjening.consumer.restpensjon.acl

/**
 * Korresponderer med HentRestPensjonerRequest i POPP
 */
data class PoppRestpensjonSpec(
    val fnr: String,
    val hentSiste: Boolean = false,
    val fomDato: String? = null,
    val tomDato: String? = null,
)