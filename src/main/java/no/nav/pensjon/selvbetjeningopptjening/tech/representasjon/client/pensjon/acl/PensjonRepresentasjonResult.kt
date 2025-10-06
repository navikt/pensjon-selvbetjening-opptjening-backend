package no.nav.pensjon.selvbetjeningopptjening.tech.representasjon.client.pensjon.acl

data class PensjonRepresentasjonResult(
    val hasValidRepresentasjonsforhold: Boolean?,
    val fullmaktsgiverNavn: String?,
    val fullmaktsgiverFnrKryptert: String?,
    val fullmaktsgiverFnr: String?
)
