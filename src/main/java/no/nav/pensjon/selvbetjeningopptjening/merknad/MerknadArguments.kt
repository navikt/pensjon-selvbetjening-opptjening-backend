package no.nav.pensjon.selvbetjeningopptjening.merknad

import no.nav.pensjon.selvbetjeningopptjening.opptjening.*
import no.nav.pensjon.selvbetjeningopptjening.person.Person

/**
 * Informasjon som behøves for å utlede merknader.
 */
data class MerknadArguments(
    val person: Person,
    val uttaksgradListe: List<Uttaksgrad>,
    val afpHistorikk: AfpHistorikk?,
    val ufoereHistorikk: UforeHistorikk?,
    val inntektListe: List<Inntekt>,
    val beholdningListe: List<Beholdning>,
    val pensjonspoengListe: List<Pensjonspoeng>,
    val restpensjonListe: List<Restpensjon>
)