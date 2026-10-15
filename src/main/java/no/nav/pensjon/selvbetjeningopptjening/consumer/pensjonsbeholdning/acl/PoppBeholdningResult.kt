package no.nav.pensjon.selvbetjeningopptjening.consumer.pensjonsbeholdning.acl

data class PoppBeholdningResult(
    val beholdninger: List<PoppBeholdning>? = null
)