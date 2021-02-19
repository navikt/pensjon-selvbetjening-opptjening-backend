package no.nav.pensjon.selvbetjeningopptjening.opptjening;

import no.nav.pensjon.selvbetjeningopptjening.model.code.MerknadCode;
import no.nav.pensjon.selvbetjeningopptjening.model.code.UforeTypeCode;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static no.nav.pensjon.selvbetjeningopptjening.model.code.MerknadCode.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MerknadHandlerTest {

    private static final int UFOREGRAD_VALUE = 100;
    private static final int YEAR = 1990;

    @Test
    void when_Omsorg_in_Pensjonspoeng_is_Null_then_setMerknadOmsorgsopptjeningPensjonspoeng_returns_empty_MerknanderList() {
        Opptjening opptjening = opptjening();
        MerknadHandler.setMerknadOmsorgsopptjeningPensjonspoeng(opptjening, getPensjonspoeng());
        assertTrue(opptjening.getMerknader().isEmpty());
    }

    @Test
    void when_Omsorgspoeng_and_Pensjonspoeng_in_OpptjeningDto_is_Null_then_setMerknadOmsorgsopptjeningPensjonspoeng_returns_empty_MerknanderList() {
        Opptjening opptjening = opptjening();
        MerknadHandler.setMerknadOmsorgsopptjeningPensjonspoeng(opptjening, pensjonspoeng());
        assertTrue(opptjening.getMerknader().isEmpty());
    }

    @Test
    void when_Omsorgspoeng_is_greater_than_Pensjonspoeng_in_OpptjeningDto_then_setMerknadOmsorgsopptjeningPensjonspoeng_returns_empty_MerknanderList() {
        Opptjening opptjening = opptjeningBasedOnOmsorgAndPensjonspoeng(100d, 10d);
        MerknadHandler.setMerknadOmsorgsopptjeningPensjonspoeng(opptjening, pensjonspoeng());
        assertTrue(opptjening.getMerknader().isEmpty());
    }

    @Test
    void when_Omsorgspoeng_is_less_than_Pensjonspoeng_in_OpptjeningDto_then_setMerknadOmsorgsopptjeningPensjonspoeng_returns_MerknadCode_OMSORGSOPPTJENING() {
        Opptjening opptjening = opptjeningBasedOnOmsorgAndPensjonspoeng(10d, 100d);
        MerknadHandler.setMerknadOmsorgsopptjeningPensjonspoeng(opptjening, pensjonspoeng());
        assertSingleMerknad(OMSORGSOPPTJENING, opptjening.getMerknader());
    }

    @Test
    void when_Omsorg_in_Pensjonspoeng_is_Null_then_setMerknadOverforOmsorgsopptjeningPensjonspoeng_returns_empty_MerknanderList() {
        Opptjening opptjening = opptjening();
        MerknadHandler.setMerknadOverforOmsorgsopptjeningPensjonspoeng(opptjening, pensjonspoeng(), emptyList());
        assertTrue(opptjening.getMerknader().isEmpty());
    }

    @Test
    void when_OmsorgType_is_not_OBU6_or_OBU7_then_setMerknadOverforOmsorgsopptjeningPensjonspoeng_returns_empty_MerknanderList() {
        Opptjening opptjening = opptjening();
        MerknadHandler.setMerknadOverforOmsorgsopptjeningPensjonspoeng(opptjening, pensjonspoeng(""), emptyList());
        assertTrue(opptjening.getMerknader().isEmpty());
    }

    @Test
    void when_OmsorgType_is_OBU6_then_setMerknadOverforOmsorgsopptjeningPensjonspoeng_returns_MerknadCode_OVERFORE_OMSORGSOPPTJENING() {
        Opptjening opptjening = opptjening();
        Pensjonspoeng pensjonspoeng = pensjonspoeng("OBU6");

        MerknadHandler.setMerknadOverforOmsorgsopptjeningPensjonspoeng(opptjening, pensjonspoeng, emptyList());

        assertSingleMerknad(OVERFORE_OMSORGSOPPTJENING, opptjening.getMerknader());
    }

    @Test
    void when_uttak_then_setMerknadOverforOmsorgsopptjeningPensjonspoeng_returns_empty_MerknanderList() {
        Opptjening opptjening = opptjening();
        Pensjonspoeng pensjonspoeng = pensjonspoeng("OBU6");

        MerknadHandler.setMerknadOverforOmsorgsopptjeningPensjonspoeng(
                opptjening,
                pensjonspoeng,
                List.of(new Uttaksgrad(1L, 100, LocalDate.of(2003, 5, 6), null)));

        assertTrue(opptjening.getMerknader().isEmpty());
    }

    @Test
    void when_OmsorgType_is_OBU7_then_setMerknadOverforOmsorgsopptjeningPensjonspoeng_returns_MerknadCode_OVERFORE_OMSORGSOPPTJENING() {
        Opptjening opptjening = opptjening();
        Pensjonspoeng pensjonspoeng = pensjonspoeng("OBU7");

        MerknadHandler.setMerknadOverforOmsorgsopptjeningPensjonspoeng(opptjening, pensjonspoeng, emptyList());

        assertSingleMerknad(OVERFORE_OMSORGSOPPTJENING, opptjening.getMerknader());
    }

    @Test
    void when_Opptjening_without_PensjonsBeholdning_and_Inntekt_then_addMerknaderOnOpptjening_returns_MerknadCode_INGEN_OPPTJENING() {
        Opptjening opptjening = opptjening();
        MerknadHandler.addMerknaderOnOpptjening(0, opptjening, null, emptyList(), null, null);
        assertSingleMerknad(INGEN_OPPTJENING, opptjening.getMerknader());
    }

    @Test
    void when_Opptjening_with_AfpHistorikk_and_without_PensjonsBeholdning_then_addMerknaderOnOpptjening_returns_MerknadCode_AFP_and_INGEN_OPPTJENING() {
        Opptjening opptjening = opptjening();

        MerknadHandler.addMerknaderOnOpptjening(YEAR, opptjening, null, emptyList(), afpHistorikk(), null);

        assertEquals(2, opptjening.getMerknader().size());
        assertEquals(AFP, opptjening.getMerknader().get(0));
        assertEquals(INGEN_OPPTJENING, opptjening.getMerknader().get(1));
    }

    @Test
    void when_UforeHistorikk_with_Uforetype_UFORE_Year_mellom_UfgFom_and_UfgTom_and_Uforegrad_then_addMerknaderOnOpptjening_returns_MerknadCode_UFOREGRAD() {
        Opptjening opptjening = opptjeningBasedOnPensjonsbeholdning();
        UforeHistorikk uforeHistorikk = uforeHistorikk(UforeTypeCode.UFORE);

        MerknadHandler.addMerknaderOnOpptjening(YEAR, opptjening, null, emptyList(), null, uforeHistorikk);

        assertSingleMerknad(UFOREGRAD, opptjening.getMerknader());
        assertEquals(UFOREGRAD_VALUE, opptjening.getMaxUforegrad());
    }

    @Test
    void when_UforeHistorikk_with_Uforetype_UFORE_Year_mellom_UfgFom_and_UfgTom_and_no_Uforegrad_then_addMerknaderOnOpptjening_returns_empty_MerknandList() {
        Opptjening opptjening = opptjeningBasedOnPensjonsbeholdning();
        UforeHistorikk uforeHistorikk = uforeHistorikk(uforeperiode());

        MerknadHandler.addMerknaderOnOpptjening(YEAR, opptjening, null, emptyList(), null, uforeHistorikk);

        assertTrue(opptjening.getMerknader().isEmpty());
    }

    @Test
    void when_UforeHistorikk_with_Uforetype_UF_M_YRKE_Year_mellom_UfgFom_and_UfgTom_and_Uforegrad_then_addMerknaderOnOpptjening_returns_MerknadCode_UFOREGRAD() {
        Opptjening opptjening = opptjeningBasedOnPensjonsbeholdning();
        UforeHistorikk uforeHistorikk = uforeHistorikk(UforeTypeCode.UF_M_YRKE);

        MerknadHandler.addMerknaderOnOpptjening(YEAR, opptjening, null, emptyList(), null, uforeHistorikk);

        assertSingleMerknad(UFOREGRAD, opptjening.getMerknader());
        assertEquals(UFOREGRAD_VALUE, opptjening.getMaxUforegrad());
    }

//    @Test
//    void when_Opptjening_with_Uttaksgrad_100_then_addMerknaderOnOpptjening_returns_MerknadCode_HELT_UTTAK() {
//        Opptjening opptjening = opptjeningBasedOnPensjonsbeholdning();
//        Uttaksgrad uttaksgrad = uttaksgrad(100);
//
//        MerknadHandler.addMerknaderOnOpptjening(YEAR, opptjening, null, singletonList(uttaksgrad), null, null);
//
//        assertSingleMerknad(HELT_UTTAK, opptjening.getMerknader());
//    }

//    @Test
//    void when_Opptjening_with_Uttaksgrad_less_than_100_then_addMerknaderOnOpptjening_returns_MerknadCode_GRADERT_UTTAK() {
//        Opptjening opptjening = opptjeningBasedOnPensjonsbeholdning();
//        Uttaksgrad uttaksgrad = uttaksgrad(40);
//
//        MerknadHandler.addMerknaderOnOpptjening(YEAR, opptjening, null, singletonList(uttaksgrad), null, null);
//
//        assertSingleMerknad(GRADERT_UTTAK, opptjening.getMerknader());
//    }

    @Test
    void when_Opptjening_with_Uttaksgrad_less_than_100_and_Year_not_mellom_Fomdato_andTomdato_then_addMerknaderOnOpptjening_returns_empty_MerknandList() {
        Opptjening opptjening = opptjeningBasedOnPensjonsbeholdning();
        Uttaksgrad uttaksgrad = uttaksgrad(40);

        MerknadHandler.addMerknaderOnOpptjening(2012, opptjening, null, singletonList(uttaksgrad), null, null);

        assertTrue(opptjening.getMerknader().isEmpty());
    }

    @Test
    void when_PensjonsBeholdning_with_year_2010_then_addMerknaderOnOpptjening_returns_MerknadCode_REFORM() {
        Opptjening opptjening = opptjening();
        MerknadHandler.addMerknaderOnOpptjening(2010, opptjening, singletonList(beholdning()), emptyList(), null, null);
        assertSingleMerknad(REFORM, opptjening.getMerknader());
    }

    @Test
    void when_OmsorgOpptjeningBelop_with_Belop_value_more_than_0_and_Year_same_as_BelopAr_then_addMerknaderOnOpptjening_returns_MerknadCode_OMSORGSOPPTJENING() {
        Opptjening opptjening = opptjeningBasedOnPensjonsbeholdning();
        Beholdning beholdning = beholdning(new Omsorgsopptjening(YEAR, 10d, null));

        MerknadHandler.addMerknaderOnOpptjening(YEAR, opptjening, singletonList(beholdning), emptyList(), null, null);

        assertSingleMerknad(OMSORGSOPPTJENING, opptjening.getMerknader());
    }

    @Test
    void when_OmsorgOpptjeningBelop_with_BelopType_OBU7_and_Year_same_as_BelopAr_with_uttak_then_addMerknaderOnOpptjening_should_only_return_OMSROGSOPPTJENING() {
        Opptjening opptjening = opptjeningBasedOnPensjonsbeholdning();
        Beholdning beholdning = beholdning("OBU7");

        List<Uttaksgrad> uttaksgradhistorkk = List.of(new Uttaksgrad(1L, 50, LocalDate.of(2000, 2, 6), null));

        MerknadHandler.addMerknaderOnOpptjening(YEAR, opptjening, singletonList(beholdning), uttaksgradhistorkk, null, null);

        assertSingleMerknad(OMSORGSOPPTJENING, opptjening.getMerknader());
    }

    @Test
    void when_OmsorgOpptjeningBelop_with_BelopType_OBU7_and_Year_same_as_BelopAr_then_addMerknaderOnOpptjening_returns_MerknadCodes_OVERFORE_OMSORGSOPPTJENING_and_OMSORGSOPPTJENING() {
        List<MerknadCode> expectedMerknads = List.of(OVERFORE_OMSORGSOPPTJENING, OMSORGSOPPTJENING);
        Opptjening opptjening = opptjeningBasedOnPensjonsbeholdning();
        Beholdning beholdning = beholdning("OBU7");

        MerknadHandler.addMerknaderOnOpptjening(YEAR, opptjening, singletonList(beholdning), emptyList(), null, null);

        assertTrue(opptjening.getMerknader().containsAll(expectedMerknads));
    }

    @Test
    void when_OmsorgOpptjeningBelop_with_BelopType_OBU6_and_Year_same_as_BelopAr_then_addMerknaderOnOpptjening_returns_MerknadCodes_OVERFORE_OMSORGSOPPTJENING_and_OMSORGSOPPTJENING() {
        List<MerknadCode> expectedMerknads = List.of(OVERFORE_OMSORGSOPPTJENING, OMSORGSOPPTJENING);
        Opptjening opptjening = opptjeningBasedOnPensjonsbeholdning();
        Beholdning beholdning = beholdning("OBU6");

        MerknadHandler.addMerknaderOnOpptjening(YEAR, opptjening, singletonList(beholdning), emptyList(), null, null);

        assertTrue(opptjening.getMerknader().containsAll(expectedMerknads));
    }

    @Test
    void when_OmsorgOpptjeningBelop_without_BelopType_and_Year_same_as_BelopAr_then_addMerknaderOnOpptjening_returns_empty_MerknadList() {
        Opptjening opptjening = opptjeningBasedOnPensjonsbeholdning();
        Beholdning beholdning = beholdning(new Omsorgsopptjening(YEAR, 0D, singletonList(new Omsorg(""))));

        MerknadHandler.addMerknaderOnOpptjening(YEAR, opptjening, singletonList(beholdning), emptyList(), null, null);

        assertTrue(opptjening.getMerknader().isEmpty());
    }

    @Test
    void when_DagpengerOpptjeningBelop_with_BelopFiskere_more_than_0_and_Year_same_as_BelopAr_then_addMerknaderOnOpptjening_returns_MerknadCode_DAGPENGER() {
        Opptjening opptjening = opptjening();
        Beholdning beholdning = beholdning(new Dagpengeopptjening(1990, null, 10d));

        MerknadHandler.addMerknaderOnOpptjening(1990, opptjening, singletonList(beholdning), emptyList(), null, null);

        assertEquals(2, opptjening.getMerknader().size());
        assertTrue(opptjening.getMerknader().contains(DAGPENGER));
    }

    @Test
    void when_DagpengerOpptjeningBelop_with_BelopOrdinar_more_than_0_and_Year_same_as_BelopAr_then_addMerknaderOnOpptjening_returns_MerknadCode_DAGPENGER() {
        Opptjening opptjening = opptjening();
        Beholdning beholdning = beholdning(new Dagpengeopptjening(1990, 10d, null));

        MerknadHandler.addMerknaderOnOpptjening(1990, opptjening, singletonList(beholdning), emptyList(), null, null);

        assertEquals(2, opptjening.getMerknader().size());
        assertTrue(opptjening.getMerknader().contains(DAGPENGER));
    }

    @Test
    void when_ForstegangstjenesteOpptjeningBelop_with_Belop_more_than_0_and_Year_same_as_BelopAr_then_addMerknaderOnOpptjening_returns_MerknadCode_FORSTEGANGSTJENESTE() {
        Opptjening opptjening = opptjening();
        Beholdning beholdning = beholdning(new Forstegangstjenesteopptjening(1990, 10d));

        MerknadHandler.addMerknaderOnOpptjening(1990, opptjening, singletonList(beholdning), emptyList(), null, null);

        assertEquals(2, opptjening.getMerknader().size());
        assertTrue(opptjening.getMerknader().contains(FORSTEGANGSTJENESTE));
    }

    private static Opptjening opptjening() {
        return new Opptjening(null, null);
    }

    private static Pensjonspoeng getPensjonspoeng() {
        return new Pensjonspoeng(null, "", null, null, null);
    }

    private static Beholdning beholdning(Forstegangstjenesteopptjening forstegangstjenesteopptjening) {
        return new Beholdning(
                null, "", "", "", null, null, LocalDate.MIN,
                null, null, null, null, null,
                "", null, null, null,
                null, forstegangstjenesteopptjening, null);
    }

    private static Beholdning beholdning(Dagpengeopptjening dagpengeopptjening) {
        return new Beholdning(
                null, "", "", "", null, null, LocalDate.MIN,
                null, null, null, null, null,
                "", null, null, null,
                dagpengeopptjening, null, null);
    }

    private static Beholdning beholdning() {
        return new Beholdning(
                null, "", "", "", null, null, LocalDate.MIN,
                null, null, null, null, null,
                "", null, null, null,
                null, null, null);
    }

    private static Beholdning beholdning(String omsorgType) {
        var omsorg = new Omsorg(omsorgType);
        var opptjening = new Omsorgsopptjening(YEAR, 100000D, singletonList(omsorg));
        return beholdning(opptjening);
    }

    private static Beholdning beholdning(Omsorgsopptjening opptjening) {
        return new Beholdning(
                null, "", "", "", null, null, LocalDate.MIN,
                null, null, null, null, null,
                "", null, null, opptjening,
                null, null, null);
    }

    private static Opptjening opptjeningBasedOnPensjonsbeholdning() {
        Opptjening opptjening = opptjening();
        opptjening.setPensjonsbeholdning(100L);
        return opptjening;
    }

    private static Opptjening opptjeningBasedOnOmsorgAndPensjonspoeng(double omsorgspoeng, double pensjonspoeng) {
        Opptjening opptjening = opptjening();
        opptjening.setOmsorgspoeng(omsorgspoeng);
        opptjening.setPensjonspoeng(pensjonspoeng);
        return opptjening;
    }

    private static Pensjonspoeng pensjonspoeng() {
        return pensjonspoeng(new Omsorg(""));
    }

    private static Pensjonspoeng pensjonspoeng(String omsorgType) {
        return pensjonspoeng(new Omsorg(omsorgType));
    }

    private static Pensjonspoeng pensjonspoeng(Omsorg omsorg) {
        return new Pensjonspoeng(null, "", null, null, omsorg);
    }

    private static Uttaksgrad uttaksgrad(int value) {
        return new Uttaksgrad(
                null,
                value,
                LocalDate.of(1980, 1, 1),
                LocalDate.of(2000, 1, 1));
    }

    private static AfpHistorikk afpHistorikk() {
        return new AfpHistorikk(LocalDate.of(1980, 1, 1), LocalDate.of(2000, 1, 1));
    }

    private static UforeHistorikk uforeHistorikk(UforeTypeCode type) {
        return new UforeHistorikk(singletonList(uforeperiode(type)));
    }

    private static UforeHistorikk uforeHistorikk(Uforeperiode uforeperiode) {
        return new UforeHistorikk(singletonList(uforeperiode));
    }

    private static Uforeperiode uforeperiode() {
        return uforeperiode(null, UforeTypeCode.UFORE);
    }

    private static Uforeperiode uforeperiode(UforeTypeCode type) {
        return uforeperiode(UFOREGRAD_VALUE, type);
    }

    private static Uforeperiode uforeperiode(Integer uforegrad, UforeTypeCode uforetype) {
        return new Uforeperiode(
                uforegrad,
                uforetype,
                LocalDate.of(1980, 1, 1),
                LocalDate.of(2000, 1, 1));
    }

    private static void assertSingleMerknad(MerknadCode expected, List<MerknadCode> actual) {
        assertEquals(1, actual.size());
        assertEquals(expected, actual.get(0));
    }
}
