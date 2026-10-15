package no.nav.pensjon.selvbetjeningopptjening.consumer.pensjonsbeholdning.acl

data class PoppBeholdningSpec(
    val fnr: String,
    val beholdningType: String = "PEN_B",
    val serviceDirectiveTPOPP006: String = "INKL_GRUNNLAG"
)