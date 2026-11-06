package no.nav.pensjon.selvbetjeningopptjening.gruppe

import no.nav.pensjon.selvbetjeningopptjening.model.code.UserGroup.*
import no.nav.pensjon.selvbetjeningopptjening.opptjening.*
import no.nav.pensjon.selvbetjeningopptjening.opptjening.beholdning.AarvisBeholdningOgMerknader
import no.nav.pensjon.selvbetjeningopptjening.opptjening.beholdning.BeholdningOgMerknaderAssemblerForBrukergruppe123
import no.nav.pensjon.selvbetjeningopptjening.opptjening.beholdning.BeholdningOgMerknaderAssemblerForBrukergruppe4
import no.nav.pensjon.selvbetjeningopptjening.opptjening.beholdning.BeholdningOgMerknaderAssemblerForBrukergruppe5
import no.nav.pensjon.selvbetjeningopptjening.opptjening.beholdning.BeholdningOgMerknaderArguments
import no.nav.pensjon.selvbetjeningopptjening.opptjening.dto.OpptjeningResponse
import no.nav.pensjon.selvbetjeningopptjening.person.Person

enum class Brukergruppe(
    val harRestpensjon: Boolean,
    val harBeholdning: Boolean = false,
    val harInntekt: Boolean = false,
    val harPensjonspoeng: Boolean = false,
    val opptjeningAssembler: (OpptjeningArguments) -> OpptjeningResponse,
    val beholdningOgMerknaderAssembler: (BeholdningOgMerknaderArguments) -> AarvisBeholdningOgMerknader,
    val aldersbeskrivelse: String
) {
    EN(
        harRestpensjon = false,
        harPensjonspoeng = true,
        opptjeningAssembler = { getOpptjeningForUserGroups123(it) },
        beholdningOgMerknaderAssembler = { beholdningOgMerknaderForBrukergruppe1og2og3(it) },
        aldersbeskrivelse = "Født før 1954"
    ),
    TO(
        harRestpensjon = true,
        harPensjonspoeng = true,
        opptjeningAssembler = { getOpptjeningForUserGroups123(it) },
        beholdningOgMerknaderAssembler = { beholdningOgMerknaderForBrukergruppe1og2og3(it) },
        aldersbeskrivelse = "Født før 1954"
    ),
    TRE(
        harRestpensjon = true,
        harPensjonspoeng = true,
        opptjeningAssembler = { getOpptjeningForUserGroups123(it) },
        beholdningOgMerknaderAssembler = { beholdningOgMerknaderForBrukergruppe1og2og3(it) },
        aldersbeskrivelse = "Født før 1954"
    ),
    FIRE(
        harRestpensjon = true,
        harBeholdning = true,
        harPensjonspoeng = true,
        opptjeningAssembler = { getOpptjeningForUserGroup4(it) },
        beholdningOgMerknaderAssembler = { beholdningOgMerknaderForBrukergruppe4(it) },
        aldersbeskrivelse = "Født 1954-1962"
    ),
    FEM(
        harRestpensjon = true,
        harBeholdning = true,
        harInntekt = true,
        opptjeningAssembler = { getOpptjeningForUserGroup5(it) },
        beholdningOgMerknaderAssembler = { beholdningOgMerknaderForBrukergruppe5(it) },
        aldersbeskrivelse = "Født etter 1962"
    );

    companion object {

        fun beholdningOgMerknaderForBrukergruppe1og2og3(args: BeholdningOgMerknaderArguments): AarvisBeholdningOgMerknader =
            beholdningOgMerknaderForBrukergruppe1og2og3(
                args.person,
                args.restpensjonListe,
                args.uttaksgradListe,
                args.afpHistorikk,
                args.ufoereHistorikk,
                args.pensjonspoengListe
            )

        fun beholdningOgMerknaderForBrukergruppe4(args: BeholdningOgMerknaderArguments): AarvisBeholdningOgMerknader =
            beholdningOgMerknaderForBrukergruppe4(
                args.person,
                args.restpensjonListe,
                args.uttaksgradListe,
                args.afpHistorikk,
                args.ufoereHistorikk,
                args.pensjonspoengListe,
                args.beholdningListe
            )

        fun beholdningOgMerknaderForBrukergruppe5(args: BeholdningOgMerknaderArguments): AarvisBeholdningOgMerknader =
            beholdningOgMerknaderForBrukergruppe5(
                args.person,
                args.restpensjonListe,
                args.uttaksgradListe,
                args.afpHistorikk,
                args.ufoereHistorikk,
                args.inntektListe,
                args.beholdningListe
            )

        private fun beholdningOgMerknaderForBrukergruppe1og2og3(
            person: Person,
            restpensjonListe: List<Restpensjon>,
            uttaksgradListe: List<Uttaksgrad>,
            afpHistorikk: AfpHistorikk?,
            ufoereHistorikk: UforeHistorikk?,
            pensjonspoengListe: List<Pensjonspoeng>
        ): AarvisBeholdningOgMerknader {
            val basis = OpptjeningBasis(
                pensjonspoengListe,
                emptyList(), // beholdninger irrelevant her
                restpensjonListe,
                emptyList(), // inntekter irrelevant her
                uttaksgradListe,
                emptyList(),
                afpHistorikk,
                ufoereHistorikk
            )

            return BeholdningOgMerknaderAssemblerForBrukergruppe123().beholdningOgMerknader(person, basis)
        }

        private fun beholdningOgMerknaderForBrukergruppe4(
            person: Person,
            restpensjonListe: List<Restpensjon>,
            uttaksgradListe: List<Uttaksgrad>,
            afpHistorikk: AfpHistorikk?,
            ufoereHistorikk: UforeHistorikk?,
            pensjonspoengListe: List<Pensjonspoeng>,
            beholdningListe: List<Beholdning>
        ): AarvisBeholdningOgMerknader {
            val basis = OpptjeningBasis(
                pensjonspoengListe,
                beholdningListe,
                restpensjonListe,
                emptyList(), // inntekter irrelevant her
                uttaksgradListe,
                emptyList(),
                afpHistorikk,
                ufoereHistorikk
            )

            return BeholdningOgMerknaderAssemblerForBrukergruppe4().beholdningOgMerknader(person, basis)
        }

        private fun beholdningOgMerknaderForBrukergruppe5(
            person: Person,
            restpensjonListe: List<Restpensjon>,
            uttaksgradListe: List<Uttaksgrad>,
            afpHistorikk: AfpHistorikk?,
            ufoereHistorikk: UforeHistorikk?,
            inntektListe: List<Inntekt>,
            beholdningListe: List<Beholdning>
        ): AarvisBeholdningOgMerknader {
            val basis = OpptjeningBasis(
                emptyList(), // pensjonspoeng irrelevant her
                beholdningListe,
                restpensjonListe,
                inntektListe,
                uttaksgradListe,
                emptyList(),
                afpHistorikk,
                ufoereHistorikk
            )

            return BeholdningOgMerknaderAssemblerForBrukergruppe5().beholdningOgMerknader(person, basis)
        }
    }
}