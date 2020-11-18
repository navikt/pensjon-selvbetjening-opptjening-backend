package no.nav.pensjon.selvbetjeningopptjening.opptjening;

import no.nav.pensjon.selvbetjeningopptjening.model.*;
import no.nav.pensjon.selvbetjeningopptjening.model.code.MerknadCode;
import no.nav.pensjon.selvbetjeningopptjening.model.code.UforeTypeCode;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.dto.OpptjeningDto;
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
        var opptjening = new OpptjeningDto();
        MerknadHandler.setMerknadOmsorgsopptjeningPensjonspoeng(opptjening, new Pensjonspoeng());
        assertTrue(opptjening.getMerknader().isEmpty());
    }

    @Test
    void when_Omsorgspoeng_and_Pensjonspoeng_in_OpptjeningDto_is_Null_then_setMerknadOmsorgsopptjeningPensjonspoeng_returns_empty_MerknanderList() {
        var opptjening = new OpptjeningDto();
        MerknadHandler.setMerknadOmsorgsopptjeningPensjonspoeng(opptjening, pensjonspoeng());
        assertTrue(opptjening.getMerknader().isEmpty());
    }

    @Test
    void when_Omsorgspoeng_is_greater_than_Pensjonspoeng_in_OpptjeningDto_then_setMerknadOmsorgsopptjeningPensjonspoeng_returns_empty_MerknanderList() {
        OpptjeningDto opptjening = opptjeningBasedOnOmsorgAndPensjonspoeng(100d, 10d);
        MerknadHandler.setMerknadOmsorgsopptjeningPensjonspoeng(opptjening, pensjonspoeng());
        assertTrue(opptjening.getMerknader().isEmpty());
    }

    @Test
    void when_Omsorgspoeng_is_less_than_Pensjonspoeng_in_OpptjeningDto_then_setMerknadOmsorgsopptjeningPensjonspoeng_returns_MerknadCode_OMSORGSOPPTJENING() {
        OpptjeningDto opptjening = opptjeningBasedOnOmsorgAndPensjonspoeng(10d, 100d);
        MerknadHandler.setMerknadOmsorgsopptjeningPensjonspoeng(opptjening, pensjonspoeng());
        assertSingleMerknad(OMSORGSOPPTJENING, opptjening.getMerknader());
    }

    @Test
    void when_Omsorg_in_Pensjonspoeng_is_Null_then_setMerknadOverforOmsorgsopptjeningPensjonspoeng_returns_empty_MerknanderList() {
        var opptjening = new OpptjeningDto();
        MerknadHandler.setMerknadOverforOmsorgsopptjeningPensjonspoeng(opptjening, pensjonspoeng());
        assertTrue(opptjening.getMerknader().isEmpty());
    }

    @Test
    void when_OmsorgType_is_not_OBU6_or_OBU7_then_setMerknadOverforOmsorgsopptjeningPensjonspoeng_returns_empty_MerknanderList() {
        var opptjening = new OpptjeningDto();
        MerknadHandler.setMerknadOverforOmsorgsopptjeningPensjonspoeng(opptjening, pensjonspoeng(""));
        assertTrue(opptjening.getMerknader().isEmpty());
    }

    @Test
    void when_OmsorgType_is_OBU6_then_setMerknadOverforOmsorgsopptjeningPensjonspoeng_returns_MerknadCode_OVERFORE_OMSORGSOPPTJENING() {
        var opptjening = new OpptjeningDto();
        Pensjonspoeng pensjonspoeng = pensjonspoeng("OBU6");

        MerknadHandler.setMerknadOverforOmsorgsopptjeningPensjonspoeng(opptjening, pensjonspoeng);

        assertSingleMerknad(OVERFORE_OMSORGSOPPTJENING, opptjening.getMerknader());
    }

    @Test
    void when_OmsorgType_is_OBU7_then_setMerknadOverforOmsorgsopptjeningPensjonspoeng_returns_MerknadCode_OVERFORE_OMSORGSOPPTJENING() {
        var opptjening = new OpptjeningDto();
        Pensjonspoeng pensjonspoeng = pensjonspoeng("OBU7");

        MerknadHandler.setMerknadOverforOmsorgsopptjeningPensjonspoeng(opptjening, pensjonspoeng);

        assertSingleMerknad(OVERFORE_OMSORGSOPPTJENING, opptjening.getMerknader());
    }

    @Test
    void when_Opptjening_without_PensjonsBeholdning_and_Inntekt_then_addMerknaderOnOpptjening_returns_MerknadCode_INGEN_OPPTJENING() {
        var opptjening = new OpptjeningDto();
        MerknadHandler.addMerknaderOnOpptjening(0, opptjening, null, emptyList(), null, null);
        assertSingleMerknad(INGEN_OPPTJENING, opptjening.getMerknader());
    }

    @Test
    void when_Opptjening_with_AfpHistorikk_and_without_PensjonsBeholdning_then_addMerknaderOnOpptjening_returns_MerknadCode_AFP_and_INGEN_OPPTJENING() {
        var opptjening = new OpptjeningDto();

        MerknadHandler.addMerknaderOnOpptjening(YEAR, opptjening, null, emptyList(), afpHistorikk(), null);

        assertEquals(2, opptjening.getMerknader().size());
        assertEquals(AFP, opptjening.getMerknader().get(0));
        assertEquals(INGEN_OPPTJENING, opptjening.getMerknader().get(1));
    }

    @Test
    void when_UforeHistorikk_with_Uforetype_UFORE_Year_mellom_UfgFom_and_UfgTom_and_Uforegrad_then_addMerknaderOnOpptjening_returns_MerknadCode_UFOREGRAD() {
        OpptjeningDto opptjening = opptjeningBasedOnPensjonsbeholdning();
        UforeHistorikk uforeHistorikk = uforeHistorikk(UforeTypeCode.UFORE);

        MerknadHandler.addMerknaderOnOpptjening(YEAR, opptjening, null, emptyList(), null, uforeHistorikk);

        assertSingleMerknad(UFOREGRAD, opptjening.getMerknader());
        assertEquals(UFOREGRAD_VALUE, opptjening.getMaksUforegrad());
    }

    @Test
    void when_UforeHistorikk_with_Uforetype_UFORE_Year_mellom_UfgFom_and_UfgTom_and_no_Uforegrad_then_addMerknaderOnOpptjening_returns_empty_MerknandList() {
        OpptjeningDto opptjening = opptjeningBasedOnPensjonsbeholdning();
        UforeHistorikk uforeHistorikk = new UforeHistorikk();
        Uforeperiode uforeperiode = new Uforeperiode();
        uforeperiode.setUfgFom(LocalDate.of(1980, 1, 1));
        uforeperiode.setUfgTom(LocalDate.of(2000, 1, 1));
        uforeperiode.setUforetype(UforeTypeCode.UFORE);
        uforeHistorikk.setUforeperiodeListe(singletonList(uforeperiode));

        MerknadHandler.addMerknaderOnOpptjening(YEAR, opptjening, null, emptyList(), null, uforeHistorikk);

        assertTrue(opptjening.getMerknader().isEmpty());
    }

    @Test
    void when_UforeHistorikk_with_Uforetype_UF_M_YRKE_Year_mellom_UfgFom_and_UfgTom_and_Uforegrad_then_addMerknaderOnOpptjening_returns_MerknadCode_UFOREGRAD() {
        OpptjeningDto opptjening = opptjeningBasedOnPensjonsbeholdning();
        UforeHistorikk uforeHistorikk = uforeHistorikk(UforeTypeCode.UF_M_YRKE);

        MerknadHandler.addMerknaderOnOpptjening(YEAR, opptjening, null, emptyList(), null, uforeHistorikk);

        assertSingleMerknad(UFOREGRAD, opptjening.getMerknader());
        assertEquals(UFOREGRAD_VALUE, opptjening.getMaksUforegrad());
    }

    @Test
    void when_Opptjening_with_Uttaksgrad_100_then_addMerknaderOnOpptjening_returns_MerknadCode_HELT_UTTAK() {
        OpptjeningDto opptjening = opptjeningBasedOnPensjonsbeholdning();
        Uttaksgrad uttaksgrad = uttaksgrad(100);

        MerknadHandler.addMerknaderOnOpptjening(YEAR, opptjening, null, singletonList(uttaksgrad), null, null);

        assertSingleMerknad(HELT_UTTAK, opptjening.getMerknader());
    }

    @Test
    void when_Opptjening_with_Uttaksgrad_less_than_100_then_addMerknaderOnOpptjening_returns_MerknadCode_GRADERT_UTTAK() {
        OpptjeningDto opptjening = opptjeningBasedOnPensjonsbeholdning();
        Uttaksgrad uttaksgrad = uttaksgrad(40);

        MerknadHandler.addMerknaderOnOpptjening(YEAR, opptjening, null, singletonList(uttaksgrad), null, null);

        assertSingleMerknad(GRADERT_UTTAK, opptjening.getMerknader());
    }

    @Test
    void when_Opptjening_with_Uttaksgrad_less_than_100_and_Year_not_mellom_Fomdato_andTomdato_then_addMerknaderOnOpptjening_returns_empty_MerknandList() {
        OpptjeningDto opptjening = opptjeningBasedOnPensjonsbeholdning();
        Uttaksgrad uttaksgrad = uttaksgrad(40);

        MerknadHandler.addMerknaderOnOpptjening(2012, opptjening, null, singletonList(uttaksgrad), null, null);

        assertTrue(opptjening.getMerknader().isEmpty());
    }

    @Test
    void when_PensjonsBeholdning_with_year_2010_then_addMerknaderOnOpptjening_returns_MerknadCode_REFORM() {
        var opptjening = new OpptjeningDto();
        MerknadHandler.addMerknaderOnOpptjening(2010, opptjening, singletonList(beholdning()), emptyList(), null, null);
        assertSingleMerknad(REFORM, opptjening.getMerknader());
    }

    @Test
    void when_DagpengerOpptjeningBelop_with_BelopFiskere_more_than_0_and_Year_same_as_BelopAr_then_addMerknaderOnOpptjening_returns_MerknadCode_DAGPENGER() {
        OpptjeningDto opptjening = opptjeningBasedOnPensjonsbeholdning();
        var dagpengeopptjening = new Dagpengeopptjening(YEAR, null, 10D);
        Beholdning beholdning = beholdning(dagpengeopptjening);

        MerknadHandler.addMerknaderOnOpptjening(YEAR, opptjening, singletonList(beholdning), emptyList(), null, null);

        assertSingleMerknad(DAGPENGER, opptjening.getMerknader());
    }

    @Test
    void when_DagpengerOpptjeningBelop_with_BelopOrdinar_more_than_0_and_Year_same_as_BelopAr_then_addMerknaderOnOpptjening_returns_MerknadCode_DAGPENGER() {
        OpptjeningDto opptjening = opptjeningBasedOnPensjonsbeholdning();
        var dagpengeopptjening = new Dagpengeopptjening(YEAR, 10D, null);
        Beholdning beholdning = beholdning(dagpengeopptjening);

        MerknadHandler.addMerknaderOnOpptjening(YEAR, opptjening, singletonList(beholdning), emptyList(), null, null);

        assertSingleMerknad(DAGPENGER, opptjening.getMerknader());
    }

    @Test
    void when_ForstegangstjenesteOpptjeningBelop_with_Belop_more_than_0_and_Year_same_as_BelopAr_then_addMerknaderOnOpptjening_returns_MerknadCode_FORSTEGANGSTJENESTE() {
        OpptjeningDto opptjening = opptjeningBasedOnPensjonsbeholdning();
        Beholdning beholdning = beholdning(new Forstegangstjenesteopptjening(YEAR, 100d));

        MerknadHandler.addMerknaderOnOpptjening(YEAR, opptjening, singletonList(beholdning), emptyList(), null, null);

        assertSingleMerknad(FORSTEGANGSTJENESTE, opptjening.getMerknader());
    }

    @Test
    void when_ForstegangstjenesteOpptjeningBelop_with_Belop_value_0_and_Year_same_as_BelopAr_then_addMerknaderOnOpptjening_returns_empty_MerknandList() {
        OpptjeningDto opptjening = opptjeningBasedOnPensjonsbeholdning();
        Beholdning beholdning = beholdning(new Forstegangstjenesteopptjening(1900, 0d));

        MerknadHandler.addMerknaderOnOpptjening(YEAR, opptjening, singletonList(beholdning), emptyList(), null, null);

        assertTrue(opptjening.getMerknader().isEmpty());
    }

    @Test
    void when_OmsorgOpptjeningBelop_with_Belop_value_more_than_0_and_Year_same_as_BelopAr_then_addMerknaderOnOpptjening_returns_MerknadCode_OMSORGSOPPTJENING() {
        OpptjeningDto opptjening = opptjeningBasedOnPensjonsbeholdning();
        Beholdning beholdning = beholdning(new Omsorgsopptjening(YEAR, 10d, null));

        MerknadHandler.addMerknaderOnOpptjening(YEAR, opptjening, singletonList(beholdning), emptyList(), null, null);

        assertSingleMerknad(OMSORGSOPPTJENING, opptjening.getMerknader());
    }

    @Test
    void when_OmsorgOpptjeningBelop_with_BelopType_OBU7_and_Year_same_as_BelopAr_then_addMerknaderOnOpptjening_returns_MerknadCode_OVERFORE_OMSORGSOPPTJENING() {
        OpptjeningDto opptjening = opptjeningBasedOnPensjonsbeholdning();
        Beholdning beholdning = beholdning("OBU7");

        MerknadHandler.addMerknaderOnOpptjening(YEAR, opptjening, singletonList(beholdning), emptyList(), null, null);

        assertSingleMerknad(OVERFORE_OMSORGSOPPTJENING, opptjening.getMerknader());
    }

    @Test
    void when_OmsorgOpptjeningBelop_with_BelopType_OBU6_and_Year_same_as_BelopAr_then_addMerknaderOnOpptjening_returns_MerknadCode_OVERFORE_OMSORGSOPPTJENING() {
        OpptjeningDto opptjening = opptjeningBasedOnPensjonsbeholdning();
        Beholdning beholdning = beholdning("OBU6");

        MerknadHandler.addMerknaderOnOpptjening(YEAR, opptjening, singletonList(beholdning), emptyList(), null, null);

        assertSingleMerknad(OVERFORE_OMSORGSOPPTJENING, opptjening.getMerknader());
    }

    @Test
    void when_OmsorgOpptjeningBelop_without_BelopType_and_Year_same_as_BelopAr_then_addMerknaderOnOpptjening_returns_empty_MerknadList() {
        OpptjeningDto opptjening = opptjeningBasedOnPensjonsbeholdning();
        Beholdning beholdning = beholdning(new Omsorgsopptjening(YEAR, 0D, singletonList(new Omsorg(""))));

        MerknadHandler.addMerknaderOnOpptjening(YEAR, opptjening, singletonList(beholdning), emptyList(), null, null);

        assertTrue(opptjening.getMerknader().isEmpty());
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
        var opptjening = new Omsorgsopptjening(YEAR, 0D, singletonList(omsorg));
        return beholdning(opptjening);
    }

    private static Beholdning beholdning(Dagpengeopptjening belop) {
        return new Beholdning(
                null, "", "", "", null, null, LocalDate.MIN,
                null, null, null, null, null,
                "", null, null, null,
                belop, null, null);
    }

    private static Beholdning beholdning(Forstegangstjenesteopptjening belop) {
        return new Beholdning(
                null, "", "", "", null, null, LocalDate.MIN,
                null, null, null, null, null,
                "", null, null, null,
                null, belop, null);
    }

    private static Beholdning beholdning(Omsorgsopptjening opptjening) {
        return new Beholdning(
                null, "", "", "", null, null, LocalDate.MIN,
                null, null, null, null, null,
                "", null, null, opptjening,
                null, null, null);
    }

    private static OpptjeningDto opptjeningBasedOnPensjonsbeholdning() {
        var opptjening = new OpptjeningDto();
        opptjening.setPensjonsbeholdning(100L);
        return opptjening;
    }

    private static OpptjeningDto opptjeningBasedOnOmsorgAndPensjonspoeng(double omsorgspoeng, double pensjonspoeng) {
        var opptjening = new OpptjeningDto();
        opptjening.setOmsorgspoeng(omsorgspoeng);
        opptjening.setPensjonspoeng(pensjonspoeng);
        return opptjening;
    }

    private static Pensjonspoeng pensjonspoeng() {
        return pensjonspoeng(new OmsorgDto());
    }

    private static Pensjonspoeng pensjonspoeng(String omsorgType) {
        return pensjonspoeng(omsorgDto(omsorgType));
    }

    private static Pensjonspoeng pensjonspoeng(OmsorgDto omsorg) {
        var pensjonspoeng = new Pensjonspoeng();
        pensjonspoeng.setOmsorg(omsorg);
        return pensjonspoeng;
    }

    private static OmsorgDto omsorgDto(String omsorgType) {
        var omsorg = new OmsorgDto();
        omsorg.setOmsorgType(omsorgType);
        return omsorg;
    }

    private static Uforeperiode uforeperiode(UforeTypeCode type) {
        var periode = new Uforeperiode();
        periode.setUfgFom(LocalDate.of(1980, 1, 1));
        periode.setUfgTom(LocalDate.of(2000, 1, 1));
        periode.setUforetype(type);
        periode.setUforegrad(UFOREGRAD_VALUE);
        return periode;
    }

    private static Uttaksgrad uttaksgrad(int value) {
        var grad = new Uttaksgrad();
        grad.setFomDato(LocalDate.of(1980, 1, 1));
        grad.setTomDato(LocalDate.of(2000, 1, 1));
        grad.setUttaksgrad(value);
        return grad;
    }

    private static AfpHistorikk afpHistorikk() {
        var historikk = new AfpHistorikk();
        historikk.setVirkFom(LocalDate.of(1980, 1, 1));
        historikk.setVirkTom(LocalDate.of(2000, 1, 1));
        return historikk;
    }

    private static UforeHistorikk uforeHistorikk(UforeTypeCode type) {
        var historikk = new UforeHistorikk();
        historikk.setUforeperiodeListe(singletonList(uforeperiode(type)));
        return historikk;
    }

    private static void assertSingleMerknad(MerknadCode expected, List<MerknadCode> actual) {
        assertEquals(1, actual.size());
        assertEquals(expected, actual.get(0));
    }
}
