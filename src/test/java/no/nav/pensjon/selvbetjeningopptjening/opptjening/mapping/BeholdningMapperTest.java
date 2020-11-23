package no.nav.pensjon.selvbetjeningopptjening.opptjening.mapping;

import no.nav.pensjon.selvbetjeningopptjening.model.*;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BeholdningMapperTest {

    @Test
    void test_that_fromDto_maps_relevant_values() {
        List<BeholdningDto> dtos = List.of(beholdning(1), beholdning(2));

        List<Beholdning> beholdninger = BeholdningMapper.fromDto(dtos);

        assertBeholdning(beholdninger.get(0), 1);
        assertBeholdning(beholdninger.get(1), 2);
    }

    private static BeholdningDto beholdning(int number) {
        var beholdning = new BeholdningDto();
        beholdning.setBeholdningId((long) number);
        beholdning.setBelop(number + .1D);
        beholdning.setBeholdningGrunnlag(number + 1.1D);
        beholdning.setBeholdningGrunnlagAvkortet(number + 2.1D);
        beholdning.setBeholdningInnskudd(number + 3.1D);
        beholdning.setBeholdningInnskuddUtenOmsorg(number + 4.1D);
        beholdning.setBeholdningType("beholdningstype" + number);
        beholdning.setFnr("1234567890" + number);
        beholdning.setStatus("status" + number);
        beholdning.setFomDato(LocalDate.of(1990 + number, number, number + 2));
        beholdning.setTomDato(LocalDate.of(1991 + number, number + 1, number + 3));
        beholdning.setVedtakId((long) (number + 1));
        beholdning.setOppdateringArsak("årsak" + number);
        beholdning.setDagpengerOpptjeningBelop(dagpengeopptjening(number));
        beholdning.setInntektOpptjeningBelop(inntektsopptjening(number));
        beholdning.setLonnsvekstregulering(lonnsvekstregulering(number));
        beholdning.setOmsorgOpptjeningBelop(omsorgsopptjening(number));
        beholdning.setForstegangstjenesteOpptjeningBelop(forstegangstjenesteopptjening(number));
        beholdning.setUforeOpptjeningBelop(uforeopptjening(number));
        return beholdning;
    }

    private static void assertBeholdning(Beholdning beholdning, int number) {
        assertEquals(number, beholdning.getId());
        assertEquals(number + .1D, beholdning.getBelop());
        assertEquals(number + 1.1D, beholdning.getGrunnlag());
        assertEquals(number + 2.1D, beholdning.getGrunnlagAvkortet());
        assertEquals(number + 3.1D, beholdning.getInnskudd());
        assertEquals(number + 4.1D, beholdning.getInnskuddUtenOmsorg());
        assertEquals("beholdningstype" + number, beholdning.getType());
        assertEquals("1234567890" + number, beholdning.getFnr());
        assertEquals("status" + number, beholdning.getStatus());
        assertEquals(LocalDate.of(1990 + number, number, number + 2), beholdning.getFomDato());
        assertEquals(LocalDate.of(1991 + number, number + 1, number + 3), beholdning.getTomDato());
        assertEquals(number + 1, beholdning.getVedtakId());
        assertEquals("årsak" + number, beholdning.getOppdateringArsak());
        assertDagpengeopptjening(beholdning.getDagpengeopptjening(), number);
        assertInntektsopptjening(beholdning.getInntektsopptjening(), number);
        assertLonnsvekstregulering(beholdning.getLonnsvekstregulering(), number);
        assertOmsorgsopptjening(beholdning.getOmsorgsopptjening(), number);
        assertForstegangstjenesteopptjening(beholdning.getForstegangstjenesteopptjening(), number);
        assertUforeopptjening(beholdning.getUforeopptjening(), number);
        // Note: No mapping for:
        // - dagpengerOpptjeningBelopId
        // - dagpengerListe
    }

    private static DagpengerOpptjeningBelopDto dagpengeopptjening(int number) {
        var opptjening = new DagpengerOpptjeningBelopDto();
        opptjening.setAr(1980 + number);
        opptjening.setBelopOrdinar(number + .2D);
        opptjening.setBelopFiskere(number + 1.2D);
        opptjening.setDagpengerOpptjeningBelopId((long) number);
        opptjening.setDagpengerListe(List.of(dagpenger(1), dagpenger(2)));
        return opptjening;
    }

    private static void assertDagpengeopptjening(Dagpengeopptjening opptjening, int number) {
        assertEquals(1980 + number, opptjening.getYear());
        assertEquals(number + .2D, opptjening.getOrdinartBelop());
        assertEquals(number + 1.2D, opptjening.getFiskerBelop());
        // Note: No mapping for:
        // - dagpengerOpptjeningBelopId
        // - dagpengerListe
    }

    private static InntektOpptjeningBelopDto inntektsopptjening(int number) {
        InntektOpptjeningBelopDto opptjening = new InntektOpptjeningBelopDto();
        opptjening.setInntektOpptjeningBelopId((long) number);
        opptjening.setAr(1990 + number);
        opptjening.setBelop(number + .1D);
        opptjening.setSumPensjonsgivendeInntekt(inntekt(number));
        opptjening.setInntektListe(List.of(inntekt(number + 1)));
        return opptjening;
    }

    private static void assertInntektsopptjening(Inntektsopptjening opptjening, int number) {
        assertEquals(1990 + number, opptjening.getYear());
        assertEquals(number + .1D, opptjening.getBelop());
        assertInntekt(opptjening.getSumPensjonsgivendeInntekt(), number);
        // Note: No mapping for:
        // - inntektOpptjeningBelopId
        // - inntektListe
    }

    private static LonnsvekstreguleringDto lonnsvekstregulering(int number) {
        var regulering = new LonnsvekstreguleringDto();
        regulering.setLonnsvekstreguleringId((long) number);
        regulering.setReguleringsbelop(number + .1D);
        regulering.setReguleringsDato(LocalDate.of(1990 + number, number + 1, number + 2));
        return regulering;
    }

    private static void assertLonnsvekstregulering(Lonnsvekstregulering regulering, int number) {
        assertEquals(number + .1D, regulering.getBelop());
        // Note: No mapping for:
        // - lonnsvekstreguleringId
        // - reguleringsDato
    }

    private static OmsorgOpptjeningBelopDto omsorgsopptjening(int number) {
        var opptjening = new OmsorgOpptjeningBelopDto();
        opptjening.setOmsorgOpptjeningBelopId((long) number);
        opptjening.setAr(1960 + number);
        opptjening.setBelop(number + .4D);
        opptjening.setOmsorgOpptjeningInnskudd(number + 1.4D);
        opptjening.setOmsorgListe(List.of(omsorg(number)));
        return opptjening;
    }

    private static void assertOmsorgsopptjening(Omsorgsopptjening opptjening, int number) {
        assertEquals(1960 + number, opptjening.getYear());
        assertEquals(number + .4D, opptjening.getBelop());
        assertOmsorg(opptjening.getOmsorger().get(0), number);
        // Note: No mapping for:
        // - omsorgOpptjeningBelopId
        // - omsorgOpptjeningInnskudd
    }

    private static ForstegangstjenesteOpptjeningBelopDto forstegangstjenesteopptjening(int number) {
        var opptjening = new ForstegangstjenesteOpptjeningBelopDto();
        opptjening.setForstegangstjenesteOpptjeningBelopId((long) number);
        opptjening.setAr(1990 + number);
        opptjening.setBelop(number + .1D);
        opptjening.setForstegangstjeneste(forstegangstjeneste());
        opptjening.setAnvendtForstegangstjenestePeriodeListe(List.of(forstegangstjenesteperiode(number)));
        return opptjening;
    }

    private static void assertForstegangstjenesteopptjening(Forstegangstjenesteopptjening opptjening, int number) {
        assertEquals(1990 + number, opptjening.getYear());
        assertEquals(number + .1D, opptjening.getBelop());
        // Note: No mapping for:
        // - forstegangstjenesteOpptjeningBelopId
        // - forstegangstjeneste
        // - anvendtForstegangstjenestePeriodeListe
    }

    private static UforeOpptjeningBelopDto uforeopptjening(int number) {
        var opptjening = new UforeOpptjeningBelopDto();
        opptjening.setUforeOpptjeningBelopId((long) number);
        opptjening.setAr(1990 + number);
        opptjening.setBelop(number + .1D);
        opptjening.setProRataBeregnetUp(number % 2 == 0);
        opptjening.setPoengtall(number + 1.1D);
        opptjening.setUforegrad(number);
        opptjening.setAntattInntekt(number + 2.1D);
        opptjening.setAntattInntektProRata(number + 3.1D);
        opptjening.setAndelProrata(number + 4.1D);
        opptjening.setPoengarTellerProRata(number + 1);
        opptjening.setPoengarNevnerProRata(number + 2);
        opptjening.setAntFremtidigArProRata(number + 3);
        opptjening.setPoengAntattArligInntekt(number + 4.1D);
        opptjening.setYrkesskadegrad(number + 4);
        opptjening.setAntattInntektYrke(number + 5.1D);
        opptjening.setUforear(number % 2 == 1);
        opptjening.setKonvertertUFT(number % 2 == 0);
        opptjening.setVeietGrunnbelop(number + 5);
        opptjening.setUforetrygd(number % 2 == 1);
        opptjening.setYrkesskade(number % 2 == 0);
        return opptjening;
    }

    private static void assertUforeopptjening(Uforeopptjening opptjening, int number) {
        assertEquals(number, opptjening.getUforegrad());
        assertEquals(number + .1D, opptjening.getBelop());
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

    private static ForstegangstjenestePeriodeDto forstegangstjenesteperiode(int number) {
        var periode = new ForstegangstjenestePeriodeDto();
        periode.setForstegangstjenestePeriodeId((long) number);
        periode.setPeriodeType("periodetype" + number);
        periode.setTjenesteType("tjenestetype" + number);
        periode.setFomDato(date(1990 + number, number, number + 2));
        periode.setTomDato(date(1991 + number, number + 1, number + 3));
        return periode;
    }

    private static ForstegangstjenesteDto forstegangstjeneste() {
        return new ForstegangstjenesteDto(); // no setters
    }

    private static OmsorgDto omsorg(int number) {
        var omsorg = new OmsorgDto();
        omsorg.setOmsorgId((long) number);
        omsorg.setFnr("1234567890" + number);
        omsorg.setFnrOmsorgFor("1234567890" + (number + 1));
        omsorg.setOmsorgType("omsorgstype" + number);
        omsorg.setKilde("kilde" + number);
        omsorg.setAr(1990 + number);
        return omsorg;
    }

    private static void assertOmsorg(Omsorg opptjening, int number) {
        assertEquals("omsorgstype" + number, opptjening.getType());
        // Note: No mapping for:
        // - omsorgId
        // - fnr
        // - fnrOmsorgFor
        // - kilde
        // - ar
    }

    private static InntektDto inntekt(int number) {
        var inntekt = new InntektDto();
        inntekt.setInntektId((long) number);
        inntekt.setFnr("1234567890" + number);
        inntekt.setKilde("kilde" + number);
        inntekt.setKommune("kommune" + number);
        inntekt.setPiMerke("merke" + number);
        inntekt.setInntektAr(1970 + number);
        inntekt.setBelop((long) (number + 1));
        inntekt.setInntektType("inntektstype" + number);
        return inntekt;
    }

    private static void assertInntekt(Inntekt inntekt, int number) {
        assertEquals(1970 + number, inntekt.getYear());
        assertEquals(number + 1, inntekt.getBelop());
        assertEquals("inntektstype" + number, inntekt.getType());
        // Note: No mapping for:
        // - inntektId
        // - fnr
        // - kilde
        // - kommune
        // - piMerke
    }

    private static DagpengerDto dagpenger(int number) {
        var dagpenger = new DagpengerDto();
        dagpenger.setAr(1990 + number);
        dagpenger.setBarnetillegg(number);
        dagpenger.setDagpengerId((long) (number + 1));
        dagpenger.setDagpengerType("dagpengetype" + number);
        dagpenger.setFerietillegg(number + 2);
        dagpenger.setFnr("1234567890" + number);
        dagpenger.setKilde("kilde" + number);
        dagpenger.setRapportType("rapporttype" + number);
        dagpenger.setUavkortetDagpengegrunnlag(number + 3);
        dagpenger.setUtbetalteDagpenger(number + 4);
        return dagpenger;
    }

    private static Date date(int year, int month, int day) {
        return new GregorianCalendar(year, month, day)
                .getTime();
    }
}
