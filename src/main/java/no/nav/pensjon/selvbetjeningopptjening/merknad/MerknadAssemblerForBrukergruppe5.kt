package no.nav.pensjon.selvbetjeningopptjening.merknad

import no.nav.pensjon.selvbetjeningopptjening.opptjening.*
import no.nav.pensjon.selvbetjeningopptjening.person.Person
import org.springframework.stereotype.Component

@Component
class MerknadAssemblerForBrukergruppe5 : OpptjeningAssembler() {

    fun merknader(person: Person, basis: OpptjeningBasis): Merknader =
        merknader(
            person,
            beholdningListe = basis.pensjonsbeholdninger.orEmpty(),
            restpensjonListe = basis.restpensjoner.orEmpty(),
            inntektListe = basis.inntekter.orEmpty(),
            afpHistorikk = basis.afpHistorikk,
            ufoereHistorikk = basis.uforeHistorikk
        )

    fun merknader(
        person: Person,
        beholdningListe: List<Beholdning>,
        restpensjonListe: List<Restpensjon>,
        inntektListe: List<Inntekt>,
        afpHistorikk: AfpHistorikk?,
        ufoereHistorikk: UforeHistorikk?
    ): Merknader {
        val opptjeningPerAar = getOpptjeningerByYear(ArrayList<Pensjonspoeng?>(), restpensjonListe)
        populatePensjonsbeholdning(opptjeningPerAar, getBeholdningerByYear(beholdningListe))

        if (opptjeningPerAar.isEmpty())
            return Merknader(perAar = emptyMap())

        val inntektPerAar = getSumPensjonsgivendeInntekterByYear(inntektListe)
        val foersteAar = getFirstYearWithOpptjening(person.getFodselsdato())
        val sisteAar = findLatestOpptjeningYear(opptjeningPerAar)
        setRestpensjoner(opptjeningPerAar, restpensjonListe)
        removeFutureOpptjening(opptjeningPerAar, sisteAar)
        putYearsWithAdditionalInntekt(inntektPerAar, opptjeningPerAar, foersteAar, sisteAar)
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