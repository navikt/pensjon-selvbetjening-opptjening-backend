package no.nav.pensjon.selvbetjeningopptjening.opptjening;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static no.nav.pensjon.selvbetjeningopptjening.model.code.MerknadCode.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import no.nav.pensjon.selvbetjeningopptjening.model.AfpHistorikk;
import no.nav.pensjon.selvbetjeningopptjening.model.Beholdning;
import no.nav.pensjon.selvbetjeningopptjening.model.DagpengerOpptjeningBelop;
import no.nav.pensjon.selvbetjeningopptjening.model.ForstegangstjenesteOpptjeningBelop;
import no.nav.pensjon.selvbetjeningopptjening.model.Omsorg;
import no.nav.pensjon.selvbetjeningopptjening.model.OmsorgOpptjeningBelop;
import no.nav.pensjon.selvbetjeningopptjening.model.Pensjonspoeng;
import no.nav.pensjon.selvbetjeningopptjening.model.UforeHistorikk;
import no.nav.pensjon.selvbetjeningopptjening.model.Uforeperiode;
import no.nav.pensjon.selvbetjeningopptjening.model.Uttaksgrad;
import no.nav.pensjon.selvbetjeningopptjening.model.code.UforeTypeCode;

class MerknadHandlerTest {

    MerknadHandler merknadHandler;

    @BeforeEach
    public void setUp(){
        merknadHandler = new MerknadHandler();
    }

    @Test
    void when_Omsorg_in_Pensjonspoeng_is_Null_then_setMerknadOmsorgsopptjeningPensjonspoeng_returns_empty_MerknanderList(){
        OpptjeningDto opptjeningDto = new OpptjeningDto();
        Pensjonspoeng pensjonspoeng = new Pensjonspoeng();

        merknadHandler.setMerknadOmsorgsopptjeningPensjonspoeng(opptjeningDto, pensjonspoeng);

        assertEquals(0, opptjeningDto.getMerknader().size());
    }

    @Test
    void when_Omsorgspoeng_and_Pensjonspoeng_in_OpptjeningDto_is_Null_then_setMerknadOmsorgsopptjeningPensjonspoeng_returns_empty_MerknanderList(){
        OpptjeningDto opptjeningDto = new OpptjeningDto();
        Pensjonspoeng pensjonspoeng = new Pensjonspoeng();
        pensjonspoeng.setOmsorg(new Omsorg());

        merknadHandler.setMerknadOmsorgsopptjeningPensjonspoeng(opptjeningDto, pensjonspoeng);

        assertEquals(0, opptjeningDto.getMerknader().size());
    }

    @Test
    void when_Omsorgspoeng_is_greater_than_Pensjonspoeng_in_OpptjeningDto_then_setMerknadOmsorgsopptjeningPensjonspoeng_returns_empty_MerknanderList(){
        Pensjonspoeng pensjonspoeng = new Pensjonspoeng();
        pensjonspoeng.setOmsorg(new Omsorg());

        OpptjeningDto opptjeningDto = new OpptjeningDto();
        opptjeningDto.setOmsorgspoeng(100d);
        opptjeningDto.setPensjonspoeng(10d);

        merknadHandler.setMerknadOmsorgsopptjeningPensjonspoeng(opptjeningDto, pensjonspoeng);

        assertEquals(0, opptjeningDto.getMerknader().size());
    }

    @Test
    void when_Omsorgspoeng_is_less_than_Pensjonspoeng_in_OpptjeningDto_then_setMerknadOmsorgsopptjeningPensjonspoeng_returns_MerknadCode_OMSORGSOPPTJENING(){
        Pensjonspoeng pensjonspoeng = new Pensjonspoeng();
        pensjonspoeng.setOmsorg(new Omsorg());

        OpptjeningDto opptjeningDto = new OpptjeningDto();
        opptjeningDto.setOmsorgspoeng(10d);
        opptjeningDto.setPensjonspoeng(100d);

        merknadHandler.setMerknadOmsorgsopptjeningPensjonspoeng(opptjeningDto, pensjonspoeng);

        assertEquals(1, opptjeningDto.getMerknader().size());
        assertEquals(OMSORGSOPPTJENING, opptjeningDto.getMerknader().get(0));
    }

    @Test
    void when_Omsorg_in_Pensjonspoeng_is_Null_then_setMerknadOverforOmsorgsopptjeningPensjonspoeng_returns_empty_MerknanderList(){
        OpptjeningDto opptjeningDto = new OpptjeningDto();
        Pensjonspoeng pensjonspoeng = new Pensjonspoeng();

        merknadHandler.setMerknadOverforOmsorgsopptjeningPensjonspoeng(opptjeningDto, pensjonspoeng);

        assertEquals(0, opptjeningDto.getMerknader().size());
    }

    @Test
    void when_OmsorgType_is_not_OBU6_or_OBU7_then_setMerknadOverforOmsorgsopptjeningPensjonspoeng_returns_empty_MerknanderList(){
        OpptjeningDto opptjeningDto = new OpptjeningDto();

        Pensjonspoeng pensjonspoeng = new Pensjonspoeng();
        Omsorg omsorg = new Omsorg();
        omsorg.setOmsorgType("");
        pensjonspoeng.setOmsorg(omsorg);

        merknadHandler.setMerknadOverforOmsorgsopptjeningPensjonspoeng(opptjeningDto, pensjonspoeng);

        assertEquals(0, opptjeningDto.getMerknader().size());
    }

    @Test
    void when_OmsorgType_is_OBU6_then_setMerknadOverforOmsorgsopptjeningPensjonspoeng_returns_MerknadCode_OVERFORE_OMSORGSOPPTJENING(){
        OpptjeningDto opptjeningDto = new OpptjeningDto();

        Pensjonspoeng pensjonspoeng = new Pensjonspoeng();
        Omsorg omsorg = new Omsorg();
        omsorg.setOmsorgType("OBU6");
        pensjonspoeng.setOmsorg(omsorg);

        merknadHandler.setMerknadOverforOmsorgsopptjeningPensjonspoeng(opptjeningDto, pensjonspoeng);

        assertEquals(1, opptjeningDto.getMerknader().size());
        assertEquals(OVERFORE_OMSORGSOPPTJENING, opptjeningDto.getMerknader().get(0));
    }

    @Test
    void when_OmsorgType_is_OBU7_then_setMerknadOverforOmsorgsopptjeningPensjonspoeng_returns_MerknadCode_OVERFORE_OMSORGSOPPTJENING(){
        OpptjeningDto opptjeningDto = new OpptjeningDto();

        Pensjonspoeng pensjonspoeng = new Pensjonspoeng();
        Omsorg omsorg = new Omsorg();
        omsorg.setOmsorgType("OBU7");
        pensjonspoeng.setOmsorg(omsorg);

        merknadHandler.setMerknadOverforOmsorgsopptjeningPensjonspoeng(opptjeningDto, pensjonspoeng);

        assertEquals(1, opptjeningDto.getMerknader().size());
        assertEquals(OVERFORE_OMSORGSOPPTJENING, opptjeningDto.getMerknader().get(0));
    }

    @Test
    void when_Opptjening_without_PensjonsBeholdning_and_Inntekt_then_addMerknaderOnOpptjening_returns_MerknadCode_INGEN_OPPTJENING(){
        OpptjeningDto opptjeningDto = new OpptjeningDto();
        List<Uttaksgrad> uttaksgradList = new ArrayList<>();

        merknadHandler.addMerknaderOnOpptjening(0, opptjeningDto, null, uttaksgradList, null, null);

        assertEquals(1, opptjeningDto.getMerknader().size());
        assertEquals(INGEN_OPPTJENING, opptjeningDto.getMerknader().get(0));
    }

    @Test
    void when_Opptjening_with_AfpHistorikk_and_without_PensjonsBeholdning_then_addMerknaderOnOpptjening_returns_MerknadCode_AFP_and_INGEN_OPPTJENING(){
        OpptjeningDto opptjeningDto = new OpptjeningDto();
        List<Uttaksgrad> uttaksgradList = new ArrayList<>();

        AfpHistorikk afpHistorikk = new AfpHistorikk();
        afpHistorikk.setVirkFom(LocalDate.of(1980, 1, 1));
        afpHistorikk.setVirkTom(LocalDate.of(2000, 1, 1));

        merknadHandler.addMerknaderOnOpptjening(1990, opptjeningDto, null, uttaksgradList, afpHistorikk, null);

        assertEquals(2, opptjeningDto.getMerknader().size());
        assertEquals(AFP, opptjeningDto.getMerknader().get(0));
        assertEquals(INGEN_OPPTJENING, opptjeningDto.getMerknader().get(1));
    }

    @Test
    void when_UforeHistorikk_with_Uforetype_UFORE_Year_mellom_UfgFom_and_UfgTom_and_Uforegrad_then_addMerknaderOnOpptjening_returns_MerknadCode_UFOREGRAD(){
        OpptjeningDto opptjeningDto = new OpptjeningDto();
        opptjeningDto.setPensjonsbeholdning(100L);
        List<Uttaksgrad> uttaksgradList = new ArrayList<>();

        UforeHistorikk uforeHistorikk = new UforeHistorikk();
        Uforeperiode uforeperiode = new Uforeperiode();
        uforeperiode.setUfgFom(LocalDate.of(1980, 1, 1));
        uforeperiode.setUfgTom(LocalDate.of(2000, 1, 1));
        uforeperiode.setUforetype(UforeTypeCode.UFORE);
        uforeperiode.setUforegrad(100);
        uforeHistorikk.setUforeperiodeListe(Collections.singletonList(uforeperiode));

        merknadHandler.addMerknaderOnOpptjening(1990, opptjeningDto, null, uttaksgradList, null, uforeHistorikk);

        assertEquals(1, opptjeningDto.getMerknader().size());
        assertEquals(UFOREGRAD, opptjeningDto.getMerknader().get(0));
        assertEquals(uforeperiode.getUforegrad(), opptjeningDto.getMaksUforegrad());
    }

    @Test
    void when_UforeHistorikk_with_Uforetype_UFORE_Year_mellom_UfgFom_and_UfgTom_and_no_Uforegrad_then_addMerknaderOnOpptjening_returns_empty_MerknandList(){
        OpptjeningDto opptjeningDto = new OpptjeningDto();
        opptjeningDto.setPensjonsbeholdning(100L);
        List<Uttaksgrad> uttaksgradList = new ArrayList<>();

        UforeHistorikk uforeHistorikk = new UforeHistorikk();
        Uforeperiode uforeperiode = new Uforeperiode();
        uforeperiode.setUfgFom(LocalDate.of(1980, 1, 1));
        uforeperiode.setUfgTom(LocalDate.of(2000, 1, 1));
        uforeperiode.setUforetype(UforeTypeCode.UFORE);
        uforeHistorikk.setUforeperiodeListe(Collections.singletonList(uforeperiode));

        merknadHandler.addMerknaderOnOpptjening(1990, opptjeningDto, null, uttaksgradList, null, uforeHistorikk);

        assertEquals(0, opptjeningDto.getMerknader().size());
    }

    @Test
    void when_UforeHistorikk_with_Uforetype_UF_M_YRKE_Year_mellom_UfgFom_and_UfgTom_and_Uforegrad_then_addMerknaderOnOpptjening_returns_MerknadCode_UFOREGRAD(){
        OpptjeningDto opptjeningDto = new OpptjeningDto();
        opptjeningDto.setPensjonsbeholdning(100L);
        List<Uttaksgrad> uttaksgradList = new ArrayList<>();

        UforeHistorikk uforeHistorikk = new UforeHistorikk();
        Uforeperiode uforeperiode = new Uforeperiode();
        uforeperiode.setUfgFom(LocalDate.of(1980, 1, 1));
        uforeperiode.setUfgTom(LocalDate.of(2000, 1, 1));
        uforeperiode.setUforetype(UforeTypeCode.UF_M_YRKE);
        uforeperiode.setUforegrad(100);
        uforeHistorikk.setUforeperiodeListe(Collections.singletonList(uforeperiode));

        merknadHandler.addMerknaderOnOpptjening(1990, opptjeningDto, null, uttaksgradList, null, uforeHistorikk);

        assertEquals(1, opptjeningDto.getMerknader().size());
        assertEquals(UFOREGRAD, opptjeningDto.getMerknader().get(0));
        assertEquals(uforeperiode.getUforegrad(), opptjeningDto.getMaksUforegrad());
    }

    @Test
    void when_Opptjening_with_Uttaksgrad_100_then_addMerknaderOnOpptjening_returns_MerknadCode_HELT_UTTAK(){
        OpptjeningDto opptjeningDto = new OpptjeningDto();
        opptjeningDto.setPensjonsbeholdning(100L);

        Uttaksgrad uttaksgrad = new Uttaksgrad();
        uttaksgrad.setFomDato(LocalDate.of(1980, 1, 1));
        uttaksgrad.setTomDato(LocalDate.of(2000, 1, 1));
        uttaksgrad.setUttaksgrad(100);
        List<Uttaksgrad> uttaksgradList = Collections.singletonList(uttaksgrad);

        merknadHandler.addMerknaderOnOpptjening(1990, opptjeningDto, null, uttaksgradList, null, null);

        assertEquals(1, opptjeningDto.getMerknader().size());
        assertEquals(HELT_UTTAK, opptjeningDto.getMerknader().get(0));
    }

    @Test
    void when_Opptjening_with_Uttaksgrad_less_than_100_then_addMerknaderOnOpptjening_returns_MerknadCode_GRADERT_UTTAK(){
        OpptjeningDto opptjeningDto = new OpptjeningDto();
        opptjeningDto.setPensjonsbeholdning(100L);

        Uttaksgrad uttaksgrad = new Uttaksgrad();
        uttaksgrad.setFomDato(LocalDate.of(1980, 1, 1));
        uttaksgrad.setTomDato(LocalDate.of(2000, 1, 1));
        uttaksgrad.setUttaksgrad(40);
        List<Uttaksgrad> uttaksgradList = Collections.singletonList(uttaksgrad);

        merknadHandler.addMerknaderOnOpptjening(1990, opptjeningDto, null, uttaksgradList, null, null);

        assertEquals(1, opptjeningDto.getMerknader().size());
        assertEquals(GRADERT_UTTAK, opptjeningDto.getMerknader().get(0));
    }

    @Test
    void when_Opptjening_with_Uttaksgrad_less_than_100_and_Year_not_mellom_Fomdato_andTomdato_then_addMerknaderOnOpptjening_returns_empty_MerknandList(){
        OpptjeningDto opptjeningDto = new OpptjeningDto();
        opptjeningDto.setPensjonsbeholdning(100L);

        Uttaksgrad uttaksgrad = new Uttaksgrad();
        uttaksgrad.setFomDato(LocalDate.of(1980, 1, 1));
        uttaksgrad.setTomDato(LocalDate.of(2000, 1, 1));
        uttaksgrad.setUttaksgrad(40);
        List<Uttaksgrad> uttaksgradList = Collections.singletonList(uttaksgrad);

        merknadHandler.addMerknaderOnOpptjening(2012, opptjeningDto, null, uttaksgradList, null, null);

        assertEquals(0, opptjeningDto.getMerknader().size());
    }

    @Test
    void when_PensjonsBeholdning_with_year_2010_then_addMerknaderOnOpptjening_returns_MerknadCode_REFORM(){
        OpptjeningDto opptjeningDto = new OpptjeningDto();

        List<Uttaksgrad> uttaksgradList = new ArrayList<>();

        Beholdning beholdning = new Beholdning();
        List<Beholdning> beholdningList = Collections.singletonList(beholdning);

        merknadHandler.addMerknaderOnOpptjening(2010, opptjeningDto, beholdningList, uttaksgradList, null, null);

        assertEquals(1, opptjeningDto.getMerknader().size());
        assertEquals(REFORM, opptjeningDto.getMerknader().get(0));
    }

    @Test
    void when_DagpengerOpptjeningBelop_with_BelopFiskere_more_than_0_and_Year_same_as_BelopAr_then_addMerknaderOnOpptjening_returns_MerknadCode_DAGPENGER(){
        OpptjeningDto opptjeningDto = new OpptjeningDto();
        opptjeningDto.setPensjonsbeholdning(100L);

        List<Uttaksgrad> uttaksgradList = new ArrayList<>();

        Beholdning beholdning = new Beholdning();
        DagpengerOpptjeningBelop dagpengerOpptjeningBelop = new DagpengerOpptjeningBelop();
        dagpengerOpptjeningBelop.setAr(1990);
        dagpengerOpptjeningBelop.setBelopFiskere(10d);
        beholdning.setDagpengerOpptjeningBelop(dagpengerOpptjeningBelop);
        List<Beholdning> beholdningList = Collections.singletonList(beholdning);

        merknadHandler.addMerknaderOnOpptjening(1990, opptjeningDto, beholdningList, uttaksgradList, null, null);

        assertEquals(1, opptjeningDto.getMerknader().size());
        assertEquals(DAGPENGER, opptjeningDto.getMerknader().get(0));
    }

    @Test
    void when_DagpengerOpptjeningBelop_with_BelopOrdinar_more_than_0_and_Year_same_as_BelopAr_then_addMerknaderOnOpptjening_returns_MerknadCode_DAGPENGER(){
        OpptjeningDto opptjeningDto = new OpptjeningDto();
        opptjeningDto.setPensjonsbeholdning(100L);

        List<Uttaksgrad> uttaksgradList = new ArrayList<>();

        Beholdning beholdning = new Beholdning();
        DagpengerOpptjeningBelop dagpengerOpptjeningBelop = new DagpengerOpptjeningBelop();
        dagpengerOpptjeningBelop.setAr(1990);
        dagpengerOpptjeningBelop.setBelopOrdinar(10d);
        beholdning.setDagpengerOpptjeningBelop(dagpengerOpptjeningBelop);
        List<Beholdning> beholdningList = Collections.singletonList(beholdning);

        merknadHandler.addMerknaderOnOpptjening(1990, opptjeningDto, beholdningList, uttaksgradList, null, null);

        assertEquals(1, opptjeningDto.getMerknader().size());
        assertEquals(DAGPENGER, opptjeningDto.getMerknader().get(0));
    }

    @Test
    void when_ForstegangstjenesteOpptjeningBelop_with_Belop_more_than_0_and_Year_same_as_BelopAr_then_addMerknaderOnOpptjening_returns_MerknadCode_FORSTEGANGSTJENESTE(){
        OpptjeningDto opptjeningDto = new OpptjeningDto();
        opptjeningDto.setPensjonsbeholdning(100L);

        List<Uttaksgrad> uttaksgradList = new ArrayList<>();

        Beholdning beholdning = new Beholdning();
        ForstegangstjenesteOpptjeningBelop fgtOpptjeningBelop = new ForstegangstjenesteOpptjeningBelop();
        fgtOpptjeningBelop.setAr(1990);
        fgtOpptjeningBelop.setBelop(100d);
        beholdning.setForstegangstjenesteOpptjeningBelop(fgtOpptjeningBelop);
        List<Beholdning> beholdningList = Collections.singletonList(beholdning);

        merknadHandler.addMerknaderOnOpptjening(1990, opptjeningDto, beholdningList, uttaksgradList, null, null);

        assertEquals(1, opptjeningDto.getMerknader().size());
        assertEquals(FORSTEGANGSTJENESTE, opptjeningDto.getMerknader().get(0));
    }

    @Test
    void when_ForstegangstjenesteOpptjeningBelop_with_Belop_value_0_and_Year_same_as_BelopAr_then_addMerknaderOnOpptjening_returns_empty_MerknandList(){
        OpptjeningDto opptjeningDto = new OpptjeningDto();
        opptjeningDto.setPensjonsbeholdning(100L);

        List<Uttaksgrad> uttaksgradList = new ArrayList<>();

        Beholdning beholdning = new Beholdning();
        ForstegangstjenesteOpptjeningBelop fgtOpptjeningBelop = new ForstegangstjenesteOpptjeningBelop();
        fgtOpptjeningBelop.setAr(1990);
        fgtOpptjeningBelop.setBelop(0d);
        beholdning.setForstegangstjenesteOpptjeningBelop(fgtOpptjeningBelop);
        List<Beholdning> beholdningList = Collections.singletonList(beholdning);

        merknadHandler.addMerknaderOnOpptjening(1990, opptjeningDto, beholdningList, uttaksgradList, null, null);

        assertEquals(0, opptjeningDto.getMerknader().size());
    }

    @Test
    void when_OmsorgOpptjeningBelop_with_Belop_value_more_than_0_and_Year_same_as_BelopAr_then_addMerknaderOnOpptjening_returns_MerknadCode_OMSORGSOPPTJENING(){
        OpptjeningDto opptjeningDto = new OpptjeningDto();
        opptjeningDto.setPensjonsbeholdning(100L);

        List<Uttaksgrad> uttaksgradList = new ArrayList<>();

        Beholdning beholdning = new Beholdning();
        OmsorgOpptjeningBelop omsorgOpptjeningBelop = new OmsorgOpptjeningBelop();
        omsorgOpptjeningBelop.setAr(1990);
        omsorgOpptjeningBelop.setBelop(10d);
        beholdning.setOmsorgOpptjeningBelop(omsorgOpptjeningBelop);
        List<Beholdning> beholdningList = Collections.singletonList(beholdning);

        merknadHandler.addMerknaderOnOpptjening(1990, opptjeningDto, beholdningList, uttaksgradList, null, null);

        assertEquals(1, opptjeningDto.getMerknader().size());
        assertEquals(OMSORGSOPPTJENING, opptjeningDto.getMerknader().get(0));
    }

    @Test
    void when_OmsorgOpptjeningBelop_with_BelopType_OBU7_and_Year_same_as_BelopAr_then_addMerknaderOnOpptjening_returns_MerknadCode_OVERFORE_OMSORGSOPPTJENING(){
        OpptjeningDto opptjeningDto = new OpptjeningDto();
        opptjeningDto.setPensjonsbeholdning(100L);

        List<Uttaksgrad> uttaksgradList = new ArrayList<>();

        Beholdning beholdning = new Beholdning();
        OmsorgOpptjeningBelop omsorgOpptjeningBelop = new OmsorgOpptjeningBelop();
        omsorgOpptjeningBelop.setAr(1990);
        Omsorg omsorg = new Omsorg();
        omsorg.setOmsorgType("OBU7");
        omsorgOpptjeningBelop.setOmsorgListe(Collections.singletonList(omsorg));
        beholdning.setOmsorgOpptjeningBelop(omsorgOpptjeningBelop);
        List<Beholdning> beholdningList = Collections.singletonList(beholdning);

        merknadHandler.addMerknaderOnOpptjening(1990, opptjeningDto, beholdningList, uttaksgradList, null, null);

        assertEquals(1, opptjeningDto.getMerknader().size());
        assertEquals(OVERFORE_OMSORGSOPPTJENING, opptjeningDto.getMerknader().get(0));
    }

    @Test
    void when_OmsorgOpptjeningBelop_with_BelopType_OBU6_and_Year_same_as_BelopAr_then_addMerknaderOnOpptjening_returns_MerknadCode_OVERFORE_OMSORGSOPPTJENING(){
        OpptjeningDto opptjeningDto = new OpptjeningDto();
        opptjeningDto.setPensjonsbeholdning(100L);

        List<Uttaksgrad> uttaksgradList = new ArrayList<>();

        Beholdning beholdning = new Beholdning();
        OmsorgOpptjeningBelop omsorgOpptjeningBelop = new OmsorgOpptjeningBelop();
        omsorgOpptjeningBelop.setAr(1990);
        Omsorg omsorg = new Omsorg();
        omsorg.setOmsorgType("OBU6");
        omsorgOpptjeningBelop.setOmsorgListe(Collections.singletonList(omsorg));
        beholdning.setOmsorgOpptjeningBelop(omsorgOpptjeningBelop);
        List<Beholdning> beholdningList = Collections.singletonList(beholdning);

        merknadHandler.addMerknaderOnOpptjening(1990, opptjeningDto, beholdningList, uttaksgradList, null, null);

        assertEquals(1, opptjeningDto.getMerknader().size());
        assertEquals(OVERFORE_OMSORGSOPPTJENING, opptjeningDto.getMerknader().get(0));
    }

    @Test
    void when_OmsorgOpptjeningBelop_without_BelopType_and_Year_same_as_BelopAr_then_addMerknaderOnOpptjening_returns_empty_MerknadList(){
        OpptjeningDto opptjeningDto = new OpptjeningDto();
        opptjeningDto.setPensjonsbeholdning(100L);

        List<Uttaksgrad> uttaksgradList = new ArrayList<>();

        Beholdning beholdning = new Beholdning();
        OmsorgOpptjeningBelop omsorgOpptjeningBelop = new OmsorgOpptjeningBelop();
        omsorgOpptjeningBelop.setAr(1990);
        Omsorg omsorg = new Omsorg();
        omsorgOpptjeningBelop.setOmsorgListe(Collections.singletonList(omsorg));
        beholdning.setOmsorgOpptjeningBelop(omsorgOpptjeningBelop);
        List<Beholdning> beholdningList = Collections.singletonList(beholdning);

        merknadHandler.addMerknaderOnOpptjening(1990, opptjeningDto, beholdningList, uttaksgradList, null, null);

        assertEquals(0, opptjeningDto.getMerknader().size());
    }
}
