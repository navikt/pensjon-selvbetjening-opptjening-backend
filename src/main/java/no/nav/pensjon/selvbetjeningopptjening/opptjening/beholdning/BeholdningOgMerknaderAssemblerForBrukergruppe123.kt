package no.nav.pensjon.selvbetjeningopptjening.opptjening.beholdning

import no.nav.pensjon.selvbetjeningopptjening.opptjening.*
import no.nav.pensjon.selvbetjeningopptjening.person.Person
import org.springframework.stereotype.Component

@Component
class BeholdningOgMerknaderAssemblerForBrukergruppe123 : OpptjeningAssembler() {

    fun beholdningOgMerknader(person: Person, basis: OpptjeningBasis): AarvisBeholdningOgMerknader =
        beholdningOgMerknader(
            person,
            pensjonspoengListe = basis.pensjonspoengList.orEmpty(),
            restpensjonListe = basis.restpensjoner.orEmpty(),
            uttaksgradListe = basis.uttaksgrader.orEmpty(),
            afpHistorikk = basis.afpHistorikk,
            ufoereHistorikk = basis.uforeHistorikk
        )

    private fun beholdningOgMerknader(
        person: Person,
        pensjonspoengListe: List<Pensjonspoeng>,
        restpensjonListe: List<Restpensjon>,
        uttaksgradListe: List<Uttaksgrad>,
        afpHistorikk: AfpHistorikk?,
        ufoereHistorikk: UforeHistorikk?
    ): AarvisBeholdningOgMerknader {
        val opptjeningPerAar: Map<Int, Opptjening> = getOpptjeningerByYear(pensjonspoengListe, restpensjonListe)
        populatePensjonspoeng(opptjeningPerAar, pensjonspoengListe, uttaksgradListe)

        if (opptjeningPerAar.isEmpty())
            return AarvisBeholdningOgMerknader(perAar = emptyMap())

        val foersteAar: Int = getFirstYearWithOpptjening(person.getFodselsdato())
        val sisteAar: Int = findLatestOpptjeningYear(opptjeningPerAar)
        setRestpensjoner(opptjeningPerAar, restpensjonListe)
        removeFutureOpptjening(opptjeningPerAar, sisteAar)
        putYearsWithNoOpptjening(opptjeningPerAar, foersteAar, sisteAar)

        return AarvisBeholdningOgMerknader(
            perAar = BeholdningOgMerknaderDeducer.perAar(
                opptjeningPerAar,
                beholdningListe = emptyList(),
                afpHistorikk,
                ufoereHistorikk,
                uttaksgradListe,
                erBrukergruppe4Eller5 = false,
                includeReformMerknad = false
            )
        )
    }
}