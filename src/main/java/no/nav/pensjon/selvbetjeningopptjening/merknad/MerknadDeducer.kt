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
    ): Map<Int, List<Merknad>> =
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
    ): List<Merknad> {
        val merknadListe = mutableListOf<Merknad>()
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

    private fun afpMerknad(aar: Int, historikk: AfpHistorikk): Merknad? =
        if (aar in historikk.startYear..sluttAar(historikk))
            Merknad(kode = MerknadCode.AFP)
        else
            null

    private fun uttakMerknader(
        aar: Int,
        uttaksgradListe: List<Uttaksgrad>,
        merknadListe: List<Merknad>
    ): List<Merknad> {
        val existing: MutableSet<Merknad> = merknadListe.toMutableSet()
        val result = mutableListOf<Merknad>()

        uttaksgradListe
            .asSequence()
            .filter { it.dekkerAaret(aar) }
            .forEach {
                uttakMerknad(it)?.run { if (existing.add(this)) result.add(this) }
            }

        return result
    }

    private fun uttakMerknad(uttaksgrad: Uttaksgrad): Merknad? =
        when {
            uttaksgrad.erGradert -> Merknad(kode = MerknadCode.GRADERT_UTTAK, uttaksgrad)
            uttaksgrad.uttaksgrad == 100 -> Merknad(kode = MerknadCode.HELT_UTTAK, uttaksgrad)
            uttaksgrad.uttaksgrad == 0 -> Merknad(kode = MerknadCode.UTTAK_STOPPET, uttaksgrad)
            else -> null
        }

    private fun omsorgsopptjeningMerknad(
        aar: Int,
        opptjening: Opptjening,
        beholdningListe: List<Beholdning>
    ): Merknad? =
        if (opptjening.hasMerknad(MerknadCode.OMSORGSOPPTJENING).not() &&
            beholdningListe.any { MerknadHandler.hasOmsorgsopptjening(aar, it) }
        )
            Merknad(kode = MerknadCode.OMSORGSOPPTJENING)
        else
            null

    private fun dagpengerMerknad(aar: Int, beholdningListe: List<Beholdning>): Merknad? =
        if (beholdningListe.any { MerknadHandler.mottattDagpenger(aar, it) })
            Merknad(kode = MerknadCode.DAGPENGER)
        else
            null

    private fun foerstegangstjenesteMerknad(aar: Int, beholdningListe: List<Beholdning>): Merknad? =
        if (beholdningListe.any { MerknadHandler.mottattForstegangstjeneste(aar, it) })
            Merknad(kode = MerknadCode.FORSTEGANGSTJENESTE)
        else
            null

    private fun ufoeregradMerknad(aar: Int, historikk: UforeHistorikk): Merknad? =
        if ((MerknadHandler.getMaxUforegrad(aar, historikk) ?: 0) > 0)
            Merknad(kode = MerknadCode.UFOREGRAD)
        else
            null

    private fun reformMerknad(aar: Int): Merknad? =
        if (aar == REFORM_AAR)
            Merknad(kode = MerknadCode.REFORM)
        else
            null

    private fun ingenOpptjeningMerknad(opptjening: Opptjening, merknadListe: List<Merknad>): Merknad? =
        if (opptjening.isPositive || merknadListe.any { it.kode == MerknadCode.REFORM })
            null
        else
            Merknad(kode = MerknadCode.INGEN_OPPTJENING)

    private fun sluttAar(historikk: AfpHistorikk): Int =
        historikk.getEndYearOrDefault { LocalDate.now().year - 1 }
}