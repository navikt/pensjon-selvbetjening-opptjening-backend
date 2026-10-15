package no.nav.pensjon.selvbetjeningopptjening.consumer.pensjonsbeholdning.acl

import no.nav.pensjon.selvbetjeningopptjening.opptjening.Uforeopptjening

data class PoppUfoereOpptjeningBeloep(
    val uforeOpptjeningBelopId: Long? = null,
    val ar: Int? = null,
    val belop: Double? = null,
    val proRataBeregnetUp: Boolean? = null,
    val poengtall: Double? = null,
    val uforegrad: Int? = null,
    val antattInntekt: Double? = null,
    val antattInntektProRata: Double? = null,
    val andelProrata: Double? = null,
    val poengarTellerProRata: Int? = null,
    val poengarNevnerProRata: Int? = null,
    val antFremtidigArProRata: Int? = null,
    val poengAntattArligInntekt: Double? = null,
    val yrkesskadegrad: Int? = null,
    val antattInntektYrke: Double? = null,
    val uforear: Boolean? = null,
    val konvertertUFT: Boolean? = null,
    val veietGrunnbelop: Int? = null,
    val uforetrygd: Boolean? = null,
    val yrkesskade: Boolean? = null
) {
    fun fromDto() =
        Uforeopptjening(uforegrad, belop)
}