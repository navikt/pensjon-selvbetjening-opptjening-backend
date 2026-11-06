package no.nav.pensjon.selvbetjeningopptjening.opptjening.beholdning

import no.nav.pensjon.selvbetjeningopptjening.merknad.Merknad
import no.nav.pensjon.selvbetjeningopptjening.merknad.Merknadkode
import no.nav.pensjon.selvbetjeningopptjening.opptjening.*
import java.time.LocalDate
import java.time.Month

object BeholdningOgMerknaderDeducer {

    private const val REFORM_AAR = 2010

    fun perAar(
        opptjeningPerAar: Map<Int, Opptjening>,
        beholdningListe: List<Beholdning>,
        afpHistorikk: AfpHistorikk?,
        ufoereHistorikk: UforeHistorikk?,
        uttaksgradListe: List<Uttaksgrad>,
        erBrukergruppe4Eller5: Boolean,
        includeReformMerknad: Boolean
    ): Map<Int, BeholdningOgMerknader> =
        opptjeningPerAar.toSortedMap().map { (aar, opptjening) ->
            aar to beholdningOgMerknader(
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

    private fun beholdningOgMerknader(
        aar: Int,
        opptjening: Opptjening,
        beholdningListe: List<Beholdning>,
        afpHistorikk: AfpHistorikk?,
        ufoereHistorikk: UforeHistorikk?,
        uttaksgradListe: List<Uttaksgrad>,
        erBrukergruppe4Eller5: Boolean,
        includeReformMerknad: Boolean
    ): BeholdningOgMerknader {
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
        val beholdning = beholdningListe.filter { it.fom.year == aar }.maxByOrNull { it.fom }

        return BeholdningOgMerknader(
            beholdning = beholdning ?: ingenBeholdning(aar),
            merknadListe = merknadListe
        )
    }

    private fun afpMerknad(aar: Int, historikk: AfpHistorikk): Merknad? =
        if (aar in historikk.startYear..sluttAar(historikk))
            Merknad(kode = Merknadkode.AFP)
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
            uttaksgrad.erGradert -> Merknad(kode = Merknadkode.GRADERT_UTTAK, uttaksgrad)
            uttaksgrad.grad == 100 -> Merknad(kode = Merknadkode.HELT_UTTAK, uttaksgrad)
            uttaksgrad.grad == 0 -> Merknad(kode = Merknadkode.UTTAK_STOPPET, uttaksgrad)
            else -> null
        }

    private fun omsorgsopptjeningMerknad(
        aar: Int,
        opptjening: Opptjening,
        beholdningListe: List<Beholdning>
    ): Merknad? =
        if (opptjening.hasMerknad(Merknadkode.OMSORGSOPPTJENING).not() &&
            beholdningListe.any { MerknadHandler.hasOmsorgsopptjening(aar, it) }
        )
            Merknad(kode = Merknadkode.OMSORGSOPPTJENING)
        else
            null

    private fun dagpengerMerknad(aar: Int, beholdningListe: List<Beholdning>): Merknad? =
        if (beholdningListe.any { MerknadHandler.mottattDagpenger(aar, it) })
            Merknad(kode = Merknadkode.DAGPENGER)
        else
            null

    private fun foerstegangstjenesteMerknad(aar: Int, beholdningListe: List<Beholdning>): Merknad? =
        if (beholdningListe.any { MerknadHandler.mottattForstegangstjeneste(aar, it) })
            Merknad(kode = Merknadkode.FORSTEGANGSTJENESTE)
        else
            null

    private fun ufoeregradMerknad(aar: Int, historikk: UforeHistorikk): Merknad? =
        if ((MerknadHandler.getMaxUforegrad(aar, historikk) ?: 0) > 0)
            Merknad(kode = Merknadkode.UFOREGRAD)
        else
            null

    private fun reformMerknad(aar: Int): Merknad? =
        if (aar == REFORM_AAR)
            Merknad(kode = Merknadkode.REFORM)
        else
            null

    private fun ingenOpptjeningMerknad(opptjening: Opptjening, merknadListe: List<Merknad>): Merknad? =
        if (opptjening.isPositive || merknadListe.any { it.kode == Merknadkode.REFORM })
            null
        else
            Merknad(kode = Merknadkode.INGEN_OPPTJENING)

    private fun sluttAar(historikk: AfpHistorikk): Int =
        historikk.getEndYearOrDefault { LocalDate.now().year - 1 }

    private fun ingenBeholdning(aar: Int) =
        Beholdning(
            beloep = 0.0,
            fom = LocalDate.of(aar, Month.JANUARY, 1)
        )
}