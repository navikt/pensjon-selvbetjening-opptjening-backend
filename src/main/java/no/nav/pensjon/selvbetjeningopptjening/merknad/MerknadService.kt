package no.nav.pensjon.selvbetjeningopptjening.merknad

import no.nav.pensjon.selvbetjeningopptjening.consumer.opptjeningsgrunnlag.OpptjeningsgrunnlagConsumer
import no.nav.pensjon.selvbetjeningopptjening.consumer.pensjonsbeholdning.PensjonsbeholdningConsumer
import no.nav.pensjon.selvbetjeningopptjening.consumer.pensjonspoeng.PensjonspoengConsumer
import no.nav.pensjon.selvbetjeningopptjening.consumer.person.PersonConsumer
import no.nav.pensjon.selvbetjeningopptjening.consumer.restpensjon.RestpensjonConsumer
import no.nav.pensjon.selvbetjeningopptjening.consumer.uttaksgrad.UttaksgradGetter
import no.nav.pensjon.selvbetjeningopptjening.gruppe.Brukergruppe
import no.nav.pensjon.selvbetjeningopptjening.opptjening.*
import no.nav.pensjon.selvbetjeningopptjening.person.Person
import no.nav.pensjon.selvbetjeningopptjening.person.PersonService
import no.nav.pensjon.selvbetjeningopptjening.person.group.UserGroupUtil.brukergruppe
import org.springframework.stereotype.Service
import java.time.LocalDate

/**
 * Henter grunnlagsdata for merknader, og utleder merknader for hvert år med opptjening.
 */
@Service
class MerknadService(
    private val personService: PersonService,
    private val personConsumer: PersonConsumer,
    private val uttaksgradGetter: UttaksgradGetter,
    private val opptjeningsgrunnlagConsumer: OpptjeningsgrunnlagConsumer,
    private val pensjonsbeholdningConsumer: PensjonsbeholdningConsumer,
    private val pensjonspoengConsumer: PensjonspoengConsumer,
    private val restpensjonConsumer: RestpensjonConsumer
) {
    fun merknaderPerAar(pid: Pid): Merknader {
        val person: Person = personService.getPerson(pid)!!
        val fnr = pid.pid
        val brukergruppe: Brukergruppe = brukergruppe(foedselsdato = person.getFodselsdato())
        val uttaksgradListe: List<Uttaksgrad> = uttaksgradGetter.getAlderSakUttaksgradhistorikkForPerson(fnr).orEmpty()

        return brukergruppe.merknadAssembler(
            MerknadArguments(
                person,
                uttaksgradListe,
                afpHistorikk = personConsumer.getAfpHistorikkForPerson(fnr),
                ufoereHistorikk = personConsumer.getUforeHistorikkForPerson(fnr),
                inntektListe = inntektListe(brukergruppe, person),
                beholdningListe = beholdningListe(brukergruppe, pid),
                pensjonspoengListe = pensjonspoengListe(brukergruppe, pid),
                restpensjonListe = restpensjonListe(brukergruppe, uttaksgradListe, pid)
            )
        )
    }

    private fun inntektListe(brukergruppe: Brukergruppe, person: Person): List<Inntekt> =
        if (brukergruppe.harInntekt)
            opptjeningsgrunnlagConsumer.getInntektListeFromOpptjeningsgrunnlag(
                person.pid.pid,
                person.getFodselsdato().year + OPPTJENING_MINSTEALDER_AAR,
                LocalDate.now().year
            ).orEmpty()
        else
            emptyList()

    private fun beholdningListe(brukergruppe: Brukergruppe, pid: Pid): List<Beholdning> =
        if (brukergruppe.harBeholdning)
            pensjonsbeholdningConsumer.getPensjonsbeholdning(pid.pid).orEmpty()
        else
            emptyList()

    private fun pensjonspoengListe(brukergruppe: Brukergruppe, pid: Pid): List<Pensjonspoeng> =
        if (brukergruppe.harPensjonspoeng)
            pensjonspoengConsumer.getPensjonspoengListe(pid.pid).orEmpty()
        else
            emptyList()

    private fun restpensjonListe(
        brukergruppe: Brukergruppe,
        uttaksgradListe: List<Uttaksgrad>,
        pid: Pid
    ): List<Restpensjon> =
        if (shouldGetRestpensjon(brukergruppe, uttaksgradListe))
            restpensjonConsumer.getRestpensjonListe(pid.pid).orEmpty()
        else
            emptyList()

    companion object {
        private const val OPPTJENING_MINSTEALDER_AAR = 13

        private fun shouldGetRestpensjon(brukergruppe: Brukergruppe, uttaksgradListe: List<Uttaksgrad>): Boolean =
            brukergruppe.harRestpensjon && uttaksgradListe.any { it.uttaksgrad < 100 }
    }
}