package no.nav.pensjon.selvbetjeningopptjening.tech.representasjon.client.pensjon.acl

data class PensjonRepresentasjonRequest(
    val representertPid: String,
    val representantPid: String?,
    val validRepresentasjonstyper: List<String>,
    val includeRepresentertNavn: Boolean
)
