package no.nav.pensjon.selvbetjeningopptjening.consumer.pensjonspoeng.acl

data class PoppPensjonspoengSpec(
    val fnr: String,
    val fomAr: Int? = null,
    val tomAr: Int? = null,
    val pensjonspoengType: String? = null
)