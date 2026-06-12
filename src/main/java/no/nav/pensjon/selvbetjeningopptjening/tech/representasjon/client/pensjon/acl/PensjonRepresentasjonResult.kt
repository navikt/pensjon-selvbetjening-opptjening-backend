package no.nav.pensjon.selvbetjeningopptjening.tech.representasjon.client.pensjon.acl

data class PensjonRepresentasjonResult(
    val hasValidRepresentasjonsforhold: Boolean,
    val representertNavn: String?,
    val representertPidKryptert: String,
    val representertPid: String
)
