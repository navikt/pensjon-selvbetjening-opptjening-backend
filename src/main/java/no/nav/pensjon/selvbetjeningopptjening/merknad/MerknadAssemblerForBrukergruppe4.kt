package no.nav.pensjon.selvbetjeningopptjening.merknad

import no.nav.pensjon.selvbetjeningopptjening.opptjening.*
import no.nav.pensjon.selvbetjeningopptjening.person.Person
import org.springframework.stereotype.Component

@Component
class MerknadAssemblerForBrukergruppe4 : OpptjeningAssembler() {

    fun merknader(person: Person, basis: OpptjeningBasis): Merknader =
        merknader(
            person,
            pensjonspoengListe = basis.pensjonspoengList.orEmpty(),
            beholdningListe = basis.pensjonsbeholdninger.orEmpty(),
            restpensjonListe = basis.restpensjoner.orEmpty(),
            uttaksgradListe = basis.uttaksgrader.orEmpty(),
            afpHistorikk = basis.afpHistorikk,
            ufoereHistorikk = basis.uforeHistorikk
        )

    private fun merknader(
        person: Person,
        pensjonspoengListe: List<Pensjonspoeng>,
        beholdningListe: List<Beholdning>,
        restpensjonListe: List<Restpensjon>,
        uttaksgradListe: List<Uttaksgrad>,
        afpHistorikk: AfpHistorikk?,
        ufoereHistorikk: UforeHistorikk?
    ): Merknader {
        val opptjeningPerAar = getOpptjeningerByYear(pensjonspoengListe, restpensjonListe)
        populatePensjonspoeng(opptjeningPerAar, pensjonspoengListe, uttaksgradListe)
        populatePensjonsbeholdning(opptjeningPerAar, getBeholdningerByYear(beholdningListe))

        if (opptjeningPerAar.isEmpty())
            return Merknader(perAar = emptyMap())

        val foersteAar = getFirstYearWithOpptjening(person.getFodselsdato())
        val sisteAar = findLatestOpptjeningYear(opptjeningPerAar)
        setRestpensjoner(opptjeningPerAar, restpensjonListe)
        removeFutureOpptjening(opptjeningPerAar, sisteAar)
        putYearsWithNoOpptjening(opptjeningPerAar, foersteAar, sisteAar)

        return Merknader(
            perAar = MerknadDeducer.merknaderPerAar(
                opptjeningPerAar,
                beholdningListe,
                afpHistorikk,
                ufoereHistorikk,
                erBrukergruppe4Eller5 = true
            )
        )
    }
}