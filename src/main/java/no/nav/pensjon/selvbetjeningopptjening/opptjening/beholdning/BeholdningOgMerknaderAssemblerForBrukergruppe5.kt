package no.nav.pensjon.selvbetjeningopptjening.opptjening.beholdning

import no.nav.pensjon.selvbetjeningopptjening.opptjening.*
import no.nav.pensjon.selvbetjeningopptjening.person.Person
import org.springframework.stereotype.Component

@Component
class BeholdningOgMerknaderAssemblerForBrukergruppe5 : OpptjeningAssembler() {

    fun beholdningOgMerknader(person: Person, basis: OpptjeningBasis): AarvisBeholdningOgMerknader =
        beholdningOgMerknader(
            person,
            beholdningListe = basis.pensjonsbeholdninger.orEmpty(),
            restpensjonListe = basis.restpensjoner.orEmpty(),
            inntektListe = basis.inntekter.orEmpty(),
            afpHistorikk = basis.afpHistorikk,
            ufoereHistorikk = basis.uforeHistorikk,
            uttaksgradListe = basis.uttaksgrader.orEmpty()
        )

    fun beholdningOgMerknader(
        person: Person,
        beholdningListe: List<Beholdning>,
        restpensjonListe: List<Restpensjon>,
        inntektListe: List<Inntekt>,
        afpHistorikk: AfpHistorikk?,
        ufoereHistorikk: UforeHistorikk?,
        uttaksgradListe: List<Uttaksgrad>
    ): AarvisBeholdningOgMerknader {
        val opptjeningPerAar = getOpptjeningerByYear(ArrayList<Pensjonspoeng?>(), restpensjonListe)
        populatePensjonsbeholdning(opptjeningPerAar, getBeholdningerByYear(beholdningListe))

        if (opptjeningPerAar.isEmpty())
            return AarvisBeholdningOgMerknader(perAar = emptyMap())

        val inntektPerAar = getSumPensjonsgivendeInntekterByYear(inntektListe)
        val foersteAar = getFirstYearWithOpptjening(person.getFodselsdato())
        val sisteAar = findLatestOpptjeningYear(opptjeningPerAar)
        setRestpensjoner(opptjeningPerAar, restpensjonListe)
        removeFutureOpptjening(opptjeningPerAar, sisteAar)
        putYearsWithAdditionalInntekt(inntektPerAar, opptjeningPerAar, foersteAar, sisteAar)
        putYearsWithNoOpptjening(opptjeningPerAar, foersteAar, sisteAar)

        return AarvisBeholdningOgMerknader(
            perAar = BeholdningOgMerknaderDeducer.perAar(
                opptjeningPerAar,
                beholdningListe,
                afpHistorikk,
                ufoereHistorikk,
                uttaksgradListe,
                erBrukergruppe4Eller5 = true,
                includeReformMerknad = false
            )
        )
    }
}