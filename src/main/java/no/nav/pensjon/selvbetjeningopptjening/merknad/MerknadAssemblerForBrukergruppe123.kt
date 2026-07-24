package no.nav.pensjon.selvbetjeningopptjening.merknad

import no.nav.pensjon.selvbetjeningopptjening.opptjening.*
import no.nav.pensjon.selvbetjeningopptjening.person.Person
import org.springframework.stereotype.Component

@Component
class MerknadAssemblerForBrukergruppe123 : OpptjeningAssembler() {

    fun merknader(person: Person, basis: OpptjeningBasis): Merknader =
        merknader(
            person,
            pensjonspoengListe = basis.pensjonspoengList.orEmpty(),
            restpensjonListe = basis.restpensjoner.orEmpty(),
            uttaksgradListe = basis.uttaksgrader.orEmpty(),
            afpHistorikk = basis.afpHistorikk,
            ufoereHistorikk = basis.uforeHistorikk
        )

    private fun merknader(
        person: Person,
        pensjonspoengListe: List<Pensjonspoeng>,
        restpensjonListe: List<Restpensjon>,
        uttaksgradListe: List<Uttaksgrad>,
        afpHistorikk: AfpHistorikk?,
        ufoereHistorikk: UforeHistorikk?
    ): Merknader {
        val opptjeningPerAar: Map<Int, Opptjening> = getOpptjeningerByYear(pensjonspoengListe, restpensjonListe)
        populatePensjonspoeng(opptjeningPerAar, pensjonspoengListe, uttaksgradListe)

        if (opptjeningPerAar.isEmpty())
            return Merknader(perAar = emptyMap())

        val foersteAar: Int = getFirstYearWithOpptjening(person.getFodselsdato())
        val sisteAar: Int = findLatestOpptjeningYear(opptjeningPerAar)
        setRestpensjoner(opptjeningPerAar, restpensjonListe)
        removeFutureOpptjening(opptjeningPerAar, sisteAar)
        putYearsWithNoOpptjening(opptjeningPerAar, foersteAar, sisteAar)

        return Merknader(
            perAar = MerknadDeducer.merknaderPerAar(
                opptjeningPerAar,
                beholdningListe = emptyList(),
                afpHistorikk,
                ufoereHistorikk,
                erBrukergruppe4Eller5 = false
            )
        )
    }
}