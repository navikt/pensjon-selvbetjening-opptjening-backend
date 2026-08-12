package no.nav.pensjon.selvbetjeningopptjening.merknad

import no.nav.pensjon.selvbetjeningopptjening.model.code.MerknadCode
import no.nav.pensjon.selvbetjeningopptjening.opptjening.*
import java.time.LocalDate

object MerknadDeducer {

    private const val REFORM_AAR = 2010

    fun merknaderPerAar(
        opptjeningPerAar: Map<Int, Opptjening>,
        beholdningListe: List<Beholdning>,
        afpHistorikk: AfpHistorikk?,
        ufoereHistorikk: UforeHistorikk?,
        uttaksgradListe: List<Uttaksgrad>,
        erBrukergruppe4Eller5: Boolean,
        includeReformMerknad: Boolean
    ): Map<Int, List<MerknadCode>> =
        opptjeningPerAar.map { (aar, opptjening) ->
            aar to merknadListe(
                aar,
                opptjening,
                beholdningListe,
                afpHistorikk,
                ufoereHistorikk,
                uttaksgradListe,
                erBrukergruppe4Eller5,
                includeReformMerknad
            )
        }.toMap()

    private fun merknadListe(
        aar: Int,
        opptjening: Opptjening,
        beholdningListe: List<Beholdning>,
        afpHistorikk: AfpHistorikk?,
        ufoereHistorikk: UforeHistorikk?,
        uttaksgradListe: List<Uttaksgrad>,
        erBrukergruppe4Eller5: Boolean,
        includeReformMerknad: Boolean
    ): List<MerknadCode> {
        val merknadListe = mutableListOf<MerknadCode>()
        afpHistorikk?.let { afpMerknad(aar, historikk = it)?.let(merknadListe::add) }
        ufoereHistorikk?.let { ufoeregradMerknad(aar, historikk = it)?.let(merknadListe::add) }

        if (erBrukergruppe4Eller5) {
            if (includeReformMerknad) {
                reformMerknad(aar)?.let(merknadListe::add)
            }
            omsorgsopptjeningMerknad(aar, opptjening, beholdningListe)?.let(merknadListe::add)
            dagpengerMerknad(aar, beholdningListe)?.let(merknadListe::add)
            foerstegangstjenesteMerknad(aar, beholdningListe)?.let(merknadListe::add)
        }

        uttakMerknader(aar, uttaksgradListe, merknadListe).let(merknadListe::addAll)
        // NB: ingenOpptjeningMerknad must be after reformMerknad
        ingenOpptjeningMerknad(opptjening, merknadListe)?.let(merknadListe::add)
        return merknadListe
    }

    private fun afpMerknad(aar: Int, historikk: AfpHistorikk): MerknadCode? =
        if (aar in historikk.startYear..sluttAar(historikk))
            MerknadCode.AFP
        else
            null

    private fun uttakMerknader(
        aar: Int,
        uttaksgradListe: List<Uttaksgrad>,
        merknadListe: List<MerknadCode>
    ): List<MerknadCode> {
        val existing = merknadListe.toMutableSet()
        val result = mutableListOf<MerknadCode>()

        uttaksgradListe
            .asSequence()
            .filter { it.coversYear(aar) }
            .forEach {
                uttakMerknad(it)?.run { if (existing.add(this)) result.add(this) }
            }

        return result
    }

    private fun uttakMerknad(uttaksgrad: Uttaksgrad): MerknadCode? =
        when {
            uttaksgrad.isGradert -> MerknadCode.GRADERT_UTTAK
            uttaksgrad.uttaksgrad == 100 -> MerknadCode.HELT_UTTAK
            else -> null
        }

    private fun omsorgsopptjeningMerknad(
        aar: Int,
        opptjening: Opptjening,
        beholdningListe: List<Beholdning>
    ): MerknadCode? =
        if (opptjening.hasMerknad(MerknadCode.OMSORGSOPPTJENING).not() &&
            beholdningListe.any { MerknadHandler.hasOmsorgsopptjening(aar, it) }
        )
            MerknadCode.OMSORGSOPPTJENING
        else
            null

    private fun dagpengerMerknad(aar: Int, beholdningListe: List<Beholdning>): MerknadCode? =
        if (beholdningListe.any { MerknadHandler.mottattDagpenger(aar, it) })
            MerknadCode.DAGPENGER
        else
            null

    private fun foerstegangstjenesteMerknad(aar: Int, beholdningListe: List<Beholdning>): MerknadCode? =
        if (beholdningListe.any { MerknadHandler.mottattForstegangstjeneste(aar, it) })
            MerknadCode.FORSTEGANGSTJENESTE
        else
            null

    private fun ufoeregradMerknad(aar: Int, historikk: UforeHistorikk): MerknadCode? =
        if ((MerknadHandler.getMaxUforegrad(aar, historikk) ?: 0) > 0)
            MerknadCode.UFOREGRAD
        else
            null

    private fun reformMerknad(aar: Int): MerknadCode? =
        if (aar == REFORM_AAR)
            MerknadCode.REFORM
        else
            null

    private fun ingenOpptjeningMerknad(opptjening: Opptjening, merknadListe: List<MerknadCode>): MerknadCode? =
        if (opptjening.isPositive || merknadListe.contains(MerknadCode.REFORM))
            null
        else
            MerknadCode.INGEN_OPPTJENING

    private fun sluttAar(historikk: AfpHistorikk): Int =
        historikk.getEndYearOrDefault { LocalDate.now().year - 1 }
}