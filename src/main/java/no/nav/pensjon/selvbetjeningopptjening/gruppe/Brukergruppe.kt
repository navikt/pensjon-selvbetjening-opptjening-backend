package no.nav.pensjon.selvbetjeningopptjening.gruppe

import no.nav.pensjon.selvbetjeningopptjening.merknad.MerknadArguments
import no.nav.pensjon.selvbetjeningopptjening.merknad.MerknadAssemblerForBrukergruppe123
import no.nav.pensjon.selvbetjeningopptjening.merknad.MerknadAssemblerForBrukergruppe4
import no.nav.pensjon.selvbetjeningopptjening.merknad.MerknadAssemblerForBrukergruppe5
import no.nav.pensjon.selvbetjeningopptjening.merknad.Merknader
import no.nav.pensjon.selvbetjeningopptjening.model.code.UserGroup.getOpptjeningForUserGroup4
import no.nav.pensjon.selvbetjeningopptjening.model.code.UserGroup.getOpptjeningForUserGroup5
import no.nav.pensjon.selvbetjeningopptjening.model.code.UserGroup.getOpptjeningForUserGroups123
import no.nav.pensjon.selvbetjeningopptjening.opptjening.*
import no.nav.pensjon.selvbetjeningopptjening.opptjening.dto.OpptjeningResponse
import no.nav.pensjon.selvbetjeningopptjening.person.Person

enum class Brukergruppe(
    val harRestpensjon: Boolean,
    val harBeholdning: Boolean = false,
    val harInntekt: Boolean = false,
    val harPensjonspoeng: Boolean = false,
    val opptjeningAssembler: (OpptjeningArguments) -> OpptjeningResponse,
    val merknadAssembler: (MerknadArguments) -> Merknader,
    val aldersbeskrivelse: String
) {
    EN(
        harRestpensjon = false,
        harPensjonspoeng = true,
        opptjeningAssembler = { getOpptjeningForUserGroups123(it) },
        merknadAssembler = { merknaderForBrukergruppe1og2og3(it) },
        aldersbeskrivelse = "Født før 1954"
    ),
    TO(
        harRestpensjon = true,
        harPensjonspoeng = true,
        opptjeningAssembler = { getOpptjeningForUserGroups123(it) },
        merknadAssembler = { merknaderForBrukergruppe1og2og3(it) },
        aldersbeskrivelse = "Født før 1954"
    ),
    TRE(
        harRestpensjon = true,
        harPensjonspoeng = true,
        opptjeningAssembler = { getOpptjeningForUserGroups123(it) },
        merknadAssembler = { merknaderForBrukergruppe1og2og3(it) },
        aldersbeskrivelse = "Født før 1954"
    ),
    FIRE(
        harRestpensjon = true,
        harBeholdning = true,
        harPensjonspoeng = true,
        opptjeningAssembler = { getOpptjeningForUserGroup4(it) },
        merknadAssembler = { merknaderForBrukergruppe4(it) },
        aldersbeskrivelse = "Født 1954-1962"
    ),
    FEM(
        harRestpensjon = true,
        harBeholdning = true,
        harInntekt = true,
        opptjeningAssembler = { getOpptjeningForUserGroup5(it) },
        merknadAssembler = { merknaderForBrukergruppe5(it) },
        aldersbeskrivelse = "Født etter 1962"
    );

    companion object {
        fun merknaderForBrukergruppe1og2og3(args: MerknadArguments): Merknader =
            merknaderForBrukergruppe1og2og3(
                args.person,
                args.restpensjonListe,
                args.uttaksgradListe,
                args.afpHistorikk,
                args.ufoereHistorikk,
                args.pensjonspoengListe
            )

        fun merknaderForBrukergruppe4(args: MerknadArguments): Merknader =
            merknaderForBrukergruppe4(
                args.person,
                args.restpensjonListe,
                args.uttaksgradListe,
                args.afpHistorikk,
                args.ufoereHistorikk,
                args.pensjonspoengListe,
                args.beholdningListe
            )

        fun merknaderForBrukergruppe5(args: MerknadArguments): Merknader =
            merknaderForBrukergruppe5(
                args.person,
                args.restpensjonListe,
                args.uttaksgradListe,
                args.afpHistorikk,
                args.ufoereHistorikk,
                args.inntektListe,
                args.beholdningListe
            )

        private fun merknaderForBrukergruppe1og2og3(
            person: Person,
            restpensjonListe: List<Restpensjon>,
            uttaksgradListe: List<Uttaksgrad>,
            afpHistorikk: AfpHistorikk?,
            ufoereHistorikk: UforeHistorikk?,
            pensjonspoengListe: List<Pensjonspoeng>
        ): Merknader {
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

            return MerknadAssemblerForBrukergruppe123().merknader(person, basis)
        }

        private fun merknaderForBrukergruppe4(
            person: Person,
            restpensjonListe: List<Restpensjon>,
            uttaksgradListe: List<Uttaksgrad>,
            afpHistorikk: AfpHistorikk?,
            ufoereHistorikk: UforeHistorikk?,
            pensjonspoengListe: List<Pensjonspoeng>,
            beholdningListe: List<Beholdning>
        ): Merknader {
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

            return MerknadAssemblerForBrukergruppe4().merknader(person, basis)
        }

        private fun merknaderForBrukergruppe5(
            person: Person,
            restpensjonListe: List<Restpensjon>,
            uttaksgradListe: List<Uttaksgrad>,
            afpHistorikk: AfpHistorikk?,
            ufoereHistorikk: UforeHistorikk?,
            inntektListe: List<Inntekt>,
            beholdningListe: List<Beholdning>
        ): Merknader {
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

            return MerknadAssemblerForBrukergruppe5().merknader(person, basis)
        }
    }
}