package no.nav.pensjon.selvbetjeningopptjening.opptjening

import java.time.LocalDate

data class Uttaksgrad(
    val vedtakId: Long = 0,
    val uttaksgrad: Int = 0,
    val fom: LocalDate,
    val tom: LocalDate? = null
) {
    private val gjelderInntilVidere: Boolean = tom == null
    val erGradert: Boolean = uttaksgrad in 1..<100

    fun dekkerAaret(aar: Int): Boolean =
        fom.year <= aar && gjelderForDetteEllerSenereAar(aar)

    private fun gjelderForDetteEllerSenereAar(aar: Int): Boolean =
        gjelderInntilVidere || aar <= tom!!.year
}
