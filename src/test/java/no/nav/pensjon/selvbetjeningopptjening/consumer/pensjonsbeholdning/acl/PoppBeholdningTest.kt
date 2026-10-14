package no.nav.pensjon.selvbetjeningopptjening.consumer.pensjonsbeholdning.acl

import io.kotest.core.spec.style.ShouldSpec
import no.nav.pensjon.selvbetjeningopptjening.opptjening.*
import org.junit.jupiter.api.Assertions.assertEquals
import java.time.LocalDate
import java.util.*

class PoppBeholdningTest : ShouldSpec({

    should("map relevant values") {
        val dtos: List<PoppBeholdning> = listOf(beholdning(1), beholdning(2))
        val beholdninger: List<Beholdning> = dtos.map { it.fromDto() }

        assertBeholdning(beholdninger[0], 1)
        assertBeholdning(beholdninger[1], 2)
    }
})

private fun beholdning(number: Int) =
    PoppBeholdning(
        beholdningId = number.toLong(),
        belop = number + .1,
        beholdningGrunnlag = number + 1.1,
        beholdningGrunnlagAvkortet = number + 2.1,
        beholdningInnskudd = number + 3.1,
        beholdningInnskuddUtenOmsorg = number + 4.1,
        beholdningType = "beholdningstype$number",
        fnr = "1234567890$number",
        status = "status$number",
        fomDato = LocalDate.of(1990 + number, number, number + 2),
        tomDato = LocalDate.of(1991 + number, number + 1, number + 3),
        vedtakId = (number + 1).toLong(),
        oppdateringArsak = "årsak$number",
        dagpengerOpptjeningBelop = dagpengeopptjening(number),
        inntektOpptjeningBelop = inntektsopptjening(number),
        lonnsvekstregulering = loennsvekstregulering(number),
        omsorgOpptjeningBelop = omsorgsopptjening(number),
        forstegangstjenesteOpptjeningBelop = foerstegangstjenesteopptjening(number),
        uforeOpptjeningBelop = ufoereopptjening(number)
    )

private fun assertBeholdning(beholdning: Beholdning, number: Int) {
    assertEquals(number.toLong(), beholdning.id)
    assertEquals(number + .1, beholdning.belop)
    assertEquals(number + 1.1, beholdning.grunnlag)
    assertEquals(number + 2.1, beholdning.grunnlagAvkortet)
    assertEquals(number + 3.1, beholdning.innskudd)
    assertEquals(number + 4.1, beholdning.innskuddUtenOmsorg)
    assertEquals("beholdningstype$number", beholdning.type)
    assertEquals("1234567890$number", beholdning.fnr)
    assertEquals("status$number", beholdning.status)
    assertEquals(LocalDate.of(1990 + number, number, number + 2), beholdning.fomDato)
    assertEquals(LocalDate.of(1991 + number, number + 1, number + 3), beholdning.tomDato)
    assertEquals((number + 1).toLong(), beholdning.vedtakId)
    assertEquals("årsak$number", beholdning.oppdateringArsak)
    assertDagpengeopptjening(beholdning.dagpengeopptjening, number)
    assertInntektsopptjening(beholdning.inntektsopptjening, number)
    assertLoennsvekstregulering(beholdning.lonnsvekstregulering, number)
    assertOmsorgsopptjening(beholdning.omsorgsopptjening, number)
    assertFoerstegangstjenesteopptjening(beholdning.forstegangstjenesteopptjening, number)
    assertUfoereopptjening(beholdning.uforeopptjening, number)
    // Note: No mapping for:
    // - dagpengerOpptjeningBelopId
    // - dagpengerListe
}

private fun dagpengeopptjening(number: Int) =
    PoppDagpengerOpptjeningBeloep(
        ar = 1980 + number,
        belopOrdinar = number + .2,
        belopFiskere = number + 1.2,
        dagpengerOpptjeningBelopId = number.toLong(),
        dagpengerListe = listOf(dagpenger(1), dagpenger(2))
    )

private fun assertDagpengeopptjening(opptjening: Dagpengeopptjening, number: Int) {
    assertEquals(1980 + number, opptjening.year)
    assertEquals(number + .2, opptjening.ordinartBelop)
    assertEquals(number + 1.2, opptjening.fiskerBelop)
    // Note: No mapping for:
    // - dagpengerOpptjeningBelopId
    // - dagpengerListe
}

private fun inntektsopptjening(number: Int) =
    PoppInntektOpptjeningBeloep(
        inntektOpptjeningBelopId = number.toLong(),
        ar = 1990 + number,
        belop = number + .1,
        sumPensjonsgivendeInntekt = inntekt(number),
        inntektListe = listOf(inntekt(number + 1)),
    )

private fun assertInntektsopptjening(opptjening: Inntektsopptjening, number: Int) {
    assertEquals(1990 + number, opptjening.year)
    assertEquals(number + .1, opptjening.belop)
    assertInntekt(opptjening.sumPensjonsgivendeInntekt, number)
    // Note: No mapping for:
    // - inntektOpptjeningBelopId
    // - inntektListe
}

private fun loennsvekstregulering(number: Int) =
    PoppLoennsvekstregulering(
        lonnsvekstreguleringId = number.toLong(),
        reguleringsbelop = number + .1,
        reguleringsDato = LocalDate.of(1990 + number, number + 1, number + 2)
    )

private fun assertLoennsvekstregulering(regulering: Lonnsvekstregulering, number: Int) {
    assertEquals(number + .1, regulering.belop)
    // Note: No mapping for:
    // - lonnsvekstreguleringId
    // - reguleringsDato
}

private fun omsorgsopptjening(number: Int) =
    PoppOmsorgOpptjeningBeloep(
        omsorgOpptjeningBelopId = number.toLong(),
        ar = 1960 + number,
        belop = number + .4,
        omsorgOpptjeningInnskudd = number + 1.4,
        omsorgListe = listOf(omsorg(number))
    )

private fun assertOmsorgsopptjening(opptjening: Omsorgsopptjening, number: Int) {
    assertEquals(1960 + number, opptjening.year)
    assertEquals(number + .4, opptjening.belop)
    assertOmsorg(opptjening.omsorger[0], number)
    // Note: No mapping for:
    // - omsorgOpptjeningBelopId
    // - omsorgOpptjeningInnskudd
}

private fun foerstegangstjenesteopptjening(number: Int) =
    PoppFoerstegangstjenesteOpptjeningBeloep(
        forstegangstjenesteOpptjeningBelopId = number.toLong(),
        ar = 1990 + number,
        belop = number + .1,
        forstegangstjeneste = PoppFoerstegangstjeneste(),
        anvendtForstegangstjenestePeriodeListe = listOf(foerstegangstjenesteperiode(number))
    )

private fun assertFoerstegangstjenesteopptjening(opptjening: Forstegangstjenesteopptjening, number: Int) {
    assertEquals(1990 + number, opptjening.year)
    assertEquals(number + .1, opptjening.belop)
    // Note: No mapping for:
    // - forstegangstjenesteOpptjeningBelopId
    // - forstegangstjeneste
    // - anvendtForstegangstjenestePeriodeListe
}

private fun ufoereopptjening(number: Int) =
    PoppUfoereOpptjeningBeloep(
        uforeOpptjeningBelopId = number.toLong(),
        ar = 1990 + number,
        belop = number + .1,
        proRataBeregnetUp = number % 2 == 0,
        poengtall = number + 1.1,
        uforegrad = number,
        antattInntekt = number + 2.1,
        antattInntektProRata = number + 3.1,
        andelProrata = number + 4.1,
        poengarTellerProRata = number + 1,
        poengarNevnerProRata = number + 2,
        antFremtidigArProRata = number + 3,
        poengAntattArligInntekt = number + 4.1,
        yrkesskadegrad = number + 4,
        antattInntektYrke = number + 5.1,
        uforear = number % 2 == 1,
        konvertertUFT = number % 2 == 0,
        veietGrunnbelop = number + 5,
        uforetrygd = number % 2 == 1,
        yrkesskade = number % 2 == 0
    )

private fun assertUfoereopptjening(opptjening: Uforeopptjening, number: Int) {
    assertEquals(number, opptjening.uforegrad)
    assertEquals(number + .1, opptjening.belop)
    // Note: No mapping for:
    // - uforeOpptjeningBelopId
    // - ar
    // - proRataBeregnetUp
    // - poengtall
    // - antattInntekt
    // - antattInntektProRata
    // - andelProrata
    // - poengarTellerProRata
    // - poengarNevnerProRata
    // - antFremtidigArProRata
    // - poengAntattArligInntekt
    // - yrkesskadegrad
    // - antattInntektYrke
    // - uforear
    // - konvertertUFT
    // - veietGrunnbelop
    // - uforetrygd
    // - yrkesskade
}

private fun foerstegangstjenesteperiode(number: Int) =
    PoppFoerstegangstjenestePeriode(
        forstegangstjenestePeriodeId = number.toLong(),
        periodeType = "periodetype$number",
        tjenesteType = "tjenestetype$number",
        fomDato = date(1990 + number, number, number + 2),
        tomDato = date(1991 + number, number + 1, number + 3)
    )

private fun omsorg(number: Int) =
    PoppOmsorg(
        omsorgId = number.toLong(),
        fnr = "1234567890$number",
        fnrOmsorgFor = "1234567890" + (number + 1),
        omsorgType = "omsorgstype$number",
        kilde = "kilde$number",
        ar = 1990 + number
    )

private fun assertOmsorg(opptjening: Omsorg, number: Int) {
    assertEquals("omsorgstype$number", opptjening.type)
    // Note: No mapping for:
    // - omsorgId
    // - fnr
    // - fnrOmsorgFor
    // - kilde
    // - ar
}

private fun inntekt(number: Int) =
    PoppInntekt(
        inntektId = number.toLong(),
        fnr = "1234567890$number",
        kilde = "kilde$number",
        kommune = "kommune$number",
        piMerke = "merke$number",
        inntektAr = 1970 + number,
        belop = (number + 1).toLong(),
        inntektType = "inntektstype$number",
    )

private fun assertInntekt(inntekt: Inntekt, number: Int) {
    assertEquals(1970 + number, inntekt.year)
    assertEquals((number + 1).toLong(), inntekt.belop)
    assertEquals("inntektstype$number", inntekt.type)
    // Note: No mapping for:
    // - inntektId
    // - fnr
    // - kilde
    // - kommune
    // - piMerke
}

private fun dagpenger(number: Int) =
    PoppDagpenger(
        ar = 1990 + number,
        barnetillegg = number,
        dagpengerId = (number + 1).toLong(),
        dagpengerType = "dagpengetype$number",
        ferietillegg = number + 2,
        fnr = "1234567890$number",
        kilde = "kilde$number",
        rapportType = "rapporttype$number",
        uavkortetDagpengegrunnlag = number + 3,
        utbetalteDagpenger = number + 4,
    )

private fun date(year: Int, month: Int, day: Int): Date =
    GregorianCalendar(year, month, day).getTime()