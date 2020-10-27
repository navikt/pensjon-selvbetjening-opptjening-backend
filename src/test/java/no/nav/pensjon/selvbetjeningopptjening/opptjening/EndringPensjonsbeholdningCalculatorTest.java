package no.nav.pensjon.selvbetjeningopptjening.opptjening;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static no.nav.pensjon.selvbetjeningopptjening.model.code.DetailsArsakCode.OPPTJENING_2012;
import static no.nav.pensjon.selvbetjeningopptjening.model.code.DetailsArsakCode.OPPTJENING_GRADERT;
import static no.nav.pensjon.selvbetjeningopptjening.model.code.GrunnlagTypeCode.DAGPENGER_GRUNNLAG;
import static no.nav.pensjon.selvbetjeningopptjening.model.code.GrunnlagTypeCode.FORSTEGANGSTJENESTE_GRUNNLAG;
import static no.nav.pensjon.selvbetjeningopptjening.model.code.GrunnlagTypeCode.INNTEKT_GRUNNLAG;
import static no.nav.pensjon.selvbetjeningopptjening.model.code.GrunnlagTypeCode.NO_GRUNNLAG;
import static no.nav.pensjon.selvbetjeningopptjening.model.code.GrunnlagTypeCode.OMSORGSOPPTJENING_GRUNNLAG;
import static no.nav.pensjon.selvbetjeningopptjening.model.code.GrunnlagTypeCode.UFORE_GRUNNLAG;
import static no.nav.pensjon.selvbetjeningopptjening.model.code.TypeArsakCode.INNGAENDE;
import static no.nav.pensjon.selvbetjeningopptjening.model.code.TypeArsakCode.INNGAENDE_2010;
import static no.nav.pensjon.selvbetjeningopptjening.model.code.TypeArsakCode.OPPTJENING;
import static no.nav.pensjon.selvbetjeningopptjening.model.code.TypeArsakCode.REGULERING;
import static no.nav.pensjon.selvbetjeningopptjening.model.code.TypeArsakCode.UTGAENDE;
import static no.nav.pensjon.selvbetjeningopptjening.model.code.TypeArsakCode.UTTAK;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import no.nav.pensjon.selvbetjeningopptjening.model.BeholdningDto;
import no.nav.pensjon.selvbetjeningopptjening.model.DagpengerOpptjeningBelop;
import no.nav.pensjon.selvbetjeningopptjening.model.ForstegangstjenesteOpptjeningBelop;
import no.nav.pensjon.selvbetjeningopptjening.model.InntektOpptjeningBelop;
import no.nav.pensjon.selvbetjeningopptjening.model.Lonnsvekstregulering;
import no.nav.pensjon.selvbetjeningopptjening.model.OmsorgOpptjeningBelop;
import no.nav.pensjon.selvbetjeningopptjening.model.UforeOpptjeningBelop;
import no.nav.pensjon.selvbetjeningopptjening.model.Uttaksgrad;
import no.nav.pensjon.selvbetjeningopptjening.model.code.DetailsArsakCode;

class EndringPensjonsbeholdningCalculatorTest {

    EndringPensjonsbeholdningCalculator endringPensjonsbeholdningCalculator;

    @BeforeEach
    public void setUp() {
        endringPensjonsbeholdningCalculator = new EndringPensjonsbeholdningCalculator();
    }

    @Test
    void when_empty_input_then_calculateEndringPensjonsbeholdning_returns_null_list() {
        List<EndringPensjonsopptjeningDto> dtos =
                endringPensjonsbeholdningCalculator.calculateEndringPensjonsbeholdning(2020, new ArrayList<>(), new ArrayList<>());

        assertNull(dtos);
    }

    @Test
    void when_beholdning_list_has_one_element_with_FomDato_GivenYear_then_calculateEndringPensjonsbeholdning_returns_2_elements() {
        BeholdningDto beholdning = new BeholdningDto();
        beholdning.setFomDato(LocalDate.of(2020, 3, 1));
        beholdning.setBelop(1d);

        List<BeholdningDto> list = Collections.singletonList(beholdning);

        List<EndringPensjonsopptjeningDto> dtos =
                endringPensjonsbeholdningCalculator.calculateEndringPensjonsbeholdning(2020, list, new ArrayList<>());

        assertEquals(2, dtos.size());
    }

    @Test
    void when_beholdning_list_has_one_element_with_FomDato_1JanGivenYear_then_calculateEndringPensjonsbeholdning_returns_2_elements() {
        BeholdningDto beholdning = new BeholdningDto();
        beholdning.setFomDato(LocalDate.of(2020, 1, 1));
        beholdning.setBelop(1d);

        List<BeholdningDto> list = Collections.singletonList(beholdning);

        List<EndringPensjonsopptjeningDto> dtos =
                endringPensjonsbeholdningCalculator.calculateEndringPensjonsbeholdning(2020, list, new ArrayList<>());

        assertEquals(2, dtos.size());
    }

    @Test
    void when_beholdning_list_has_one_element_with_FomDato_1JanGivenYear_and_TomDato_31DecGivenYear_then_calculateEndringPensjonsbeholdning_returns_3_elements() {
        BeholdningDto beholdning = new BeholdningDto();
        beholdning.setFomDato(LocalDate.of(2020, 1, 1));
        beholdning.setTomDato(LocalDate.of(2020, 12, 31));
        beholdning.setBelop(1d);

        List<BeholdningDto> list = Collections.singletonList(beholdning);

        List<EndringPensjonsopptjeningDto> dtos =
                endringPensjonsbeholdningCalculator.calculateEndringPensjonsbeholdning(2020, list, new ArrayList<>());

        assertEquals(3, dtos.size());
    }

    @Test
    void when_beholdning_with_FomDato_GivenYear_then_calculateEndringPensjonsbeholdning_returns_2_elements_med_endringBelop_og_pensjonsbeholdningBelop() {
        BeholdningDto beholdning = new BeholdningDto();
        beholdning.setFomDato(LocalDate.of(2020, 2, 1));
        beholdning.setBelop(10d);

        List<EndringPensjonsopptjeningDto> dtos =
                endringPensjonsbeholdningCalculator.calculateEndringPensjonsbeholdning(2020, Collections.singletonList(beholdning), new ArrayList<>());

        assertEquals(2, dtos.size());
        assertNull(dtos.get(0).getEndringBelop());
        assertEquals(0, dtos.get(0).getPensjonsbeholdningBelop());
        assertEquals(beholdning.getBelop(), dtos.get(1).getEndringBelop());
        assertEquals(beholdning.getBelop(), dtos.get(1).getPensjonsbeholdningBelop());
    }

    @Test
    void when_beholdning_with_FomDato_1JanGivenYear_and_uttak_then_calculateEndringPensjonsbeholdning_returns_3_elements_med_endringBelop_og_pensjonsbeholdningBelop() {
        LocalDate fomDato = LocalDate.of(2020, 1, 1);
        BeholdningDto beholdning = new BeholdningDto();
        beholdning.setFomDato(fomDato);
        beholdning.setBelop(10d);

        Uttaksgrad uttaksgrad = new Uttaksgrad();
        uttaksgrad.setFomDato(fomDato);

        List<EndringPensjonsopptjeningDto> dtos =
                endringPensjonsbeholdningCalculator.calculateEndringPensjonsbeholdning(2020, Collections.singletonList(beholdning), Collections.singletonList(uttaksgrad));

        assertEquals(3, dtos.size());
        assertNull(dtos.get(0).getEndringBelop());
        assertEquals(0, dtos.get(0).getPensjonsbeholdningBelop());
        assertEquals(0, dtos.get(1).getPensjonsbeholdningBelop());
        assertEquals(0, dtos.get(1).getEndringBelop());
        assertEquals(beholdning.getBelop(), dtos.get(2).getEndringBelop());
        assertEquals(beholdning.getBelop(), dtos.get(2).getPensjonsbeholdningBelop());
    }

    @Test
    void when_beholdning_with_FomDato_1JanGivenYear_with_BeholdningInnskudd_and_uttak_then_calculateEndringPensjonsbeholdning_returns_3_elements_med_endringBelop_og_pensjonsbeholdningBelop() {
        LocalDate fomDato = LocalDate.of(2020, 1, 1);
        BeholdningDto beholdning = new BeholdningDto();
        beholdning.setFomDato(fomDato);
        beholdning.setBelop(100d);
        beholdning.setBeholdningInnskudd(10d);

        Uttaksgrad uttaksgrad = new Uttaksgrad();
        uttaksgrad.setFomDato(fomDato);

        List<EndringPensjonsopptjeningDto> dtos =
                endringPensjonsbeholdningCalculator.calculateEndringPensjonsbeholdning(2020, Collections.singletonList(beholdning), Collections.singletonList(uttaksgrad));

        assertEquals(3, dtos.size());
        assertNull(dtos.get(0).getEndringBelop());
        assertEquals(0, dtos.get(0).getPensjonsbeholdningBelop());
        assertEquals(beholdning.getBeholdningInnskudd(), dtos.get(1).getPensjonsbeholdningBelop());
        assertEquals(beholdning.getBeholdningInnskudd(), dtos.get(1).getEndringBelop());
        assertEquals(beholdning.getBelop() - beholdning.getBeholdningInnskudd(), dtos.get(2).getEndringBelop());
        assertEquals(beholdning.getBelop(), dtos.get(2).getPensjonsbeholdningBelop());
    }

    @Test
    void when_beholdning_with_FomDato_1JanGivenYear_and_TomDato_31DecGivenYear_and_uttak_then_calculateEndringPensjonsbeholdning_returns_4_elements_med_endringBelop_og_pensjonsbeholdningBelop() {
        LocalDate fomDato = LocalDate.of(2020, 1, 1);
        LocalDate tomDato = LocalDate.of(2020, 12, 31);
        BeholdningDto beholdning = new BeholdningDto();
        beholdning.setFomDato(fomDato);
        beholdning.setTomDato(tomDato);
        beholdning.setBelop(10d);

        Uttaksgrad uttaksgrad = new Uttaksgrad();
        uttaksgrad.setFomDato(fomDato);
        uttaksgrad.setTomDato(tomDato);

        List<EndringPensjonsopptjeningDto> dtos =
                endringPensjonsbeholdningCalculator.calculateEndringPensjonsbeholdning(2020, Collections.singletonList(beholdning), Collections.singletonList(uttaksgrad));

        assertEquals(4, dtos.size());
        assertNull(dtos.get(0).getEndringBelop());
        assertEquals(0, dtos.get(0).getPensjonsbeholdningBelop());
        assertEquals(0, dtos.get(1).getPensjonsbeholdningBelop());
        assertEquals(0, dtos.get(1).getEndringBelop());
        assertEquals(beholdning.getBelop(), dtos.get(2).getEndringBelop());
        assertEquals(beholdning.getBelop(), dtos.get(2).getPensjonsbeholdningBelop());
        assertNull(dtos.get(3).getEndringBelop());
        assertEquals(beholdning.getBelop(), dtos.get(3).getPensjonsbeholdningBelop());
    }

    @Test
    void when_beholdning_list_has_one_element_with_FomDato_1MayGivenYear_with_Lonnsvekstregulering_and_uttak_then_calculateEndringPensjonsbeholdning_returns_3_elements() {
        LocalDate fomDato = LocalDate.of(2020, 5, 1);
        BeholdningDto beholdning = new BeholdningDto();
        beholdning.setFomDato(fomDato);
        beholdning.setBelop(100d);
        beholdning.setLonnsvekstregulering(new Lonnsvekstregulering());
        beholdning.getLonnsvekstregulering().setReguleringsbelop(10d);

        List<BeholdningDto> list = Collections.singletonList(beholdning);

        Uttaksgrad uttaksgrad = new Uttaksgrad();
        uttaksgrad.setFomDato(fomDato);

        List<EndringPensjonsopptjeningDto> dtos =
                endringPensjonsbeholdningCalculator.calculateEndringPensjonsbeholdning(2020, list, Collections.singletonList(uttaksgrad));

        assertEquals(3, dtos.size());
        assertNull(dtos.get(0).getEndringBelop());
        assertEquals(0, dtos.get(0).getPensjonsbeholdningBelop());
        assertEquals(beholdning.getLonnsvekstregulering().getReguleringsbelop(), dtos.get(1).getPensjonsbeholdningBelop());
        assertEquals(beholdning.getLonnsvekstregulering().getReguleringsbelop(), dtos.get(1).getEndringBelop());
        assertEquals(beholdning.getBelop() - beholdning.getLonnsvekstregulering().getReguleringsbelop(), dtos.get(2).getEndringBelop());
        assertEquals(beholdning.getBelop(), dtos.get(2).getPensjonsbeholdningBelop());
    }

    @Test
    void when_GivenYear_2020_with_uttaksgrad_value_100_then_addNyOpptjening_returns_ArsakDetailCode_OPPTJENING_HEL() {
        BeholdningDto beholdning = new BeholdningDto();
        beholdning.setFomDato(LocalDate.of(2020, 1, 1));
        beholdning.setBelop(1d);
        beholdning.setVedtakId(1L);

        Uttaksgrad uttaksgrad = new Uttaksgrad();
        uttaksgrad.setVedtakId(1L);
        uttaksgrad.setUttaksgrad(100);
        uttaksgrad.setFomDato(LocalDate.of(2019, 1, 1));
        uttaksgrad.setTomDato(LocalDate.of(2020, 12, 31));

        List<BeholdningDto> list = Collections.singletonList(beholdning);

        List<EndringPensjonsopptjeningDto> dtos =
                endringPensjonsbeholdningCalculator.calculateEndringPensjonsbeholdning(2020, list, Collections.singletonList(uttaksgrad));

        assertEquals(DetailsArsakCode.OPPTJENING_HEL, dtos.get(1).getArsakDetails().get(0));
        assertEquals(OPPTJENING, dtos.get(1).getArsakType());
    }

    @Test
    void when_GivenYear_2020_with_uttaksgrad_value_lessthan100_then_addNyOpptjening_returns_ArsakDetailCode_OPPTJENING_GRADERT() {
        BeholdningDto beholdning = new BeholdningDto();
        beholdning.setFomDato(LocalDate.of(2020, 1, 1));
        beholdning.setBelop(1d);
        beholdning.setVedtakId(1L);

        Uttaksgrad uttaksgrad = new Uttaksgrad();
        uttaksgrad.setVedtakId(1L);
        uttaksgrad.setUttaksgrad(50);
        uttaksgrad.setFomDato(LocalDate.of(2019, 1, 1));
        uttaksgrad.setTomDato(LocalDate.of(2020, 12, 31));

        List<BeholdningDto> list = Collections.singletonList(beholdning);

        List<EndringPensjonsopptjeningDto> dtos =
                endringPensjonsbeholdningCalculator.calculateEndringPensjonsbeholdning(2020, list, Collections.singletonList(uttaksgrad));

        assertEquals(OPPTJENING_GRADERT, dtos.get(1).getArsakDetails().get(0));
        assertEquals(OPPTJENING, dtos.get(1).getArsakType());
    }

    @Test
    void when_FomDato_1Jan2020_with_uttaksgrad_value_0_then_addNyOpptjening_returns_ArsakDetailCode_OPPTJENING_2012() {
        BeholdningDto beholdning = new BeholdningDto();
        beholdning.setFomDato(LocalDate.of(2020, 1, 1));
        beholdning.setBelop(1d);
        beholdning.setVedtakId(1L);

        Uttaksgrad uttaksgrad = new Uttaksgrad();
        uttaksgrad.setVedtakId(1L);
        uttaksgrad.setUttaksgrad(0);
        uttaksgrad.setFomDato(LocalDate.of(2019, 1, 1));
        uttaksgrad.setTomDato(LocalDate.of(2020, 12, 31));

        List<BeholdningDto> list = Collections.singletonList(beholdning);

        List<EndringPensjonsopptjeningDto> dtos =
                endringPensjonsbeholdningCalculator.calculateEndringPensjonsbeholdning(2020, list, Collections.singletonList(uttaksgrad));

        assertEquals(OPPTJENING_2012, dtos.get(1).getArsakDetails().get(0));
        assertEquals(OPPTJENING, dtos.get(1).getArsakType());
    }

    @Test
    void when_FomDato_1MayGivenYear_with_Lonnsvekstregulering_then_addRegulering_returns_ArsakDetailCode_REGULERING() {
        BeholdningDto beholdning = new BeholdningDto();
        beholdning.setFomDato(LocalDate.of(2020, 5, 1));
        beholdning.setLonnsvekstregulering(new Lonnsvekstregulering());
        beholdning.getLonnsvekstregulering().setReguleringsbelop(2d);
        beholdning.setBelop(1d);

        List<BeholdningDto> list = Collections.singletonList(beholdning);

        List<EndringPensjonsopptjeningDto> dtos =
                endringPensjonsbeholdningCalculator.calculateEndringPensjonsbeholdning(2020, list, new ArrayList<>());

        assertEquals(DetailsArsakCode.REGULERING, dtos.get(1).getArsakDetails().get(0));
        assertEquals(REGULERING, dtos.get(1).getArsakType());
    }

    @Test
    void when_FomDato_1JanGivenYear_then_addInngaendeBeholdning_and_addNyOpptjening_returns_2_ArsakType_values() {
        BeholdningDto beholdning = new BeholdningDto();
        beholdning.setFomDato(LocalDate.of(2020, 1, 1));
        beholdning.setBelop(1d);

        List<BeholdningDto> list = Collections.singletonList(beholdning);

        List<EndringPensjonsopptjeningDto> dtos =
                endringPensjonsbeholdningCalculator.calculateEndringPensjonsbeholdning(2020, list, new ArrayList<>());

        assertEquals(INNGAENDE, dtos.get(0).getArsakType());
        assertEquals(OPPTJENING, dtos.get(1).getArsakType());
    }

    @Test
    void when_FomDato_1JanGivenYear_and_GivenYear_2010_then_addInngaendeBeholdning_and_addNyOpptjening_returns_2_ArsakType_values() {
        BeholdningDto beholdning = new BeholdningDto();
        beholdning.setFomDato(LocalDate.of(2010, 1, 1));
        beholdning.setBelop(1d);

        List<BeholdningDto> list = Collections.singletonList(beholdning);

        List<EndringPensjonsopptjeningDto> dtos =
                endringPensjonsbeholdningCalculator.calculateEndringPensjonsbeholdning(2010, list, new ArrayList<>());

        assertEquals(INNGAENDE, dtos.get(0).getArsakType());
        assertEquals(INNGAENDE_2010, dtos.get(1).getArsakType());
    }

    @Test
    void when_FomDato_1JanGivenYear_and_TomDato_31DecGivenYear_then_addInngaendeBeholdning_and_addNyOpptjening_and_addUtgaendeBeholdning_returns_3_ArsakType_values() {
        BeholdningDto beholdning = new BeholdningDto();
        beholdning.setFomDato(LocalDate.of(2020, 1, 1));
        beholdning.setTomDato(LocalDate.of(2020, 12, 31));
        beholdning.setBelop(1d);

        List<BeholdningDto> list = Collections.singletonList(beholdning);

        List<EndringPensjonsopptjeningDto> dtos =
                endringPensjonsbeholdningCalculator.calculateEndringPensjonsbeholdning(2020, list, new ArrayList<>());

        assertEquals(INNGAENDE, dtos.get(0).getArsakType());
        assertEquals(OPPTJENING, dtos.get(1).getArsakType());
        assertEquals(UTGAENDE, dtos.get(2).getArsakType());
    }

    @Test
    void when_FomDato_Before_1st_May_GivenYear_then_addInngaendeBeholdning_and_addChangesUttaksgradBeforeReguleringAtMay1th_returns_2_ArsakType_values() {
        BeholdningDto beholdning = new BeholdningDto();
        beholdning.setFomDato(LocalDate.of(2020, 3, 1));
        beholdning.setBelop(1d);

        List<BeholdningDto> list = Collections.singletonList(beholdning);

        List<EndringPensjonsopptjeningDto> dtos =
                endringPensjonsbeholdningCalculator.calculateEndringPensjonsbeholdning(2020, list, new ArrayList<>());

        assertEquals(INNGAENDE, dtos.get(0).getArsakType());
        assertEquals(UTTAK, dtos.get(1).getArsakType());
    }

    @Test
    void when_FomDato_After_1st_May_GivenYear_then_addInngaendeBeholdning_and_addChangesUttaksgradAfterReguleringAtMay1th_returns_2_ArsakType_values() {
        BeholdningDto beholdning = new BeholdningDto();
        beholdning.setFomDato(LocalDate.of(2020, 6, 1));
        beholdning.setBelop(1d);

        List<BeholdningDto> list = Collections.singletonList(beholdning);

        List<EndringPensjonsopptjeningDto> dtos =
                endringPensjonsbeholdningCalculator.calculateEndringPensjonsbeholdning(2020, list, new ArrayList<>());

        assertEquals(INNGAENDE, dtos.get(0).getArsakType());
        assertEquals(UTTAK, dtos.get(1).getArsakType());
    }

    @Test
    void when_FomDato_1MayGivenYear_with_Lonnsvekstregulering_then_addInngaendeBeholdning_and_addRegulering_returns_2_ArsakType_values() {
        BeholdningDto beholdning = new BeholdningDto();
        beholdning.setFomDato(LocalDate.of(2020, 5, 1));
        beholdning.setLonnsvekstregulering(new Lonnsvekstregulering());
        beholdning.getLonnsvekstregulering().setReguleringsbelop(10d);
        beholdning.setBelop(1d);
        beholdning.setBeholdningInnskudd(10d);

        List<BeholdningDto> list = Collections.singletonList(beholdning);

        List<EndringPensjonsopptjeningDto> dtos =
                endringPensjonsbeholdningCalculator.calculateEndringPensjonsbeholdning(2020, list, new ArrayList<>());

        assertEquals(INNGAENDE, dtos.get(0).getArsakType());
        assertEquals(REGULERING, dtos.get(1).getArsakType());
    }

    @Test
    void when_beholdningGrunnlag_is_inntekt_then_add_grunnlagTypeCode_INNTEKT_GRUNNLAG() {
        double inntekt = 1d;

        List<BeholdningDto> list = Collections.singletonList(
                constructBeholdningWithOpptjeningBelop(inntekt, inntekt, 2d, 0d, 0d, 0d));

        List<EndringPensjonsopptjeningDto> dtos =
                endringPensjonsbeholdningCalculator.calculateEndringPensjonsbeholdning(2020, list, new ArrayList<>());

        assertEquals(OPPTJENING, dtos.get(1).getArsakType());
        assertEquals(1, dtos.get(1).getGrunnlagTypes().size());
        assertTrue(dtos.get(1).getGrunnlagTypes().contains(INNTEKT_GRUNNLAG));
        assertEquals(inntekt, dtos.get(1).getGrunnlag());
    }

    @Test
    void when_beholdningGrunnlag_is_omsorg_then_add_grunnlagTypeCode_OMSORGSOPPTJENING_GRUNNLAG() {
        double omsorg = 1d;

        List<BeholdningDto> list = Collections.singletonList(
                constructBeholdningWithOpptjeningBelop(omsorg, 2d, omsorg, 0d, 0d, 0d));

        List<EndringPensjonsopptjeningDto> dtos =
                endringPensjonsbeholdningCalculator.calculateEndringPensjonsbeholdning(2020, list, new ArrayList<>());

        assertEquals(OPPTJENING, dtos.get(1).getArsakType());
        assertEquals(1, dtos.get(1).getGrunnlagTypes().size());
        assertTrue(dtos.get(1).getGrunnlagTypes().contains(OMSORGSOPPTJENING_GRUNNLAG));
        assertEquals(omsorg, dtos.get(1).getGrunnlag());
    }

    @Test
    void when_beholdningGrunnlag_is_forstegangstjeneste_then_add_grunnlagTypeCode_FORSTEGANGSTJENESTE_GRUNNLAG() {
        double forstegangstjeneste = 1d;

        List<BeholdningDto> list = Collections.singletonList(
                constructBeholdningWithOpptjeningBelop(forstegangstjeneste, 0d, 0d, forstegangstjeneste, 0d, 0d));

        List<EndringPensjonsopptjeningDto> dtos =
                endringPensjonsbeholdningCalculator.calculateEndringPensjonsbeholdning(2020, list, new ArrayList<>());

        assertEquals(OPPTJENING, dtos.get(1).getArsakType());
        assertEquals(1, dtos.get(1).getGrunnlagTypes().size());
        assertTrue(dtos.get(1).getGrunnlagTypes().contains(FORSTEGANGSTJENESTE_GRUNNLAG));
        assertEquals(forstegangstjeneste, dtos.get(1).getGrunnlag());
    }

    @Test
    void when_beholdningGrunnlag_is_dagpenger_then_add_grunnlagTypeCode_DAGPENGER_GRUNNLAG() {
        double dagpenger = 1d;

        List<BeholdningDto> list = Collections.singletonList(
                constructBeholdningWithOpptjeningBelop(dagpenger, 0d, 0d, 0d, dagpenger, 0d));

        List<EndringPensjonsopptjeningDto> dtos =
                endringPensjonsbeholdningCalculator.calculateEndringPensjonsbeholdning(2020, list, new ArrayList<>());

        assertEquals(OPPTJENING, dtos.get(1).getArsakType());
        assertEquals(1, dtos.get(1).getGrunnlagTypes().size());
        assertTrue(dtos.get(1).getGrunnlagTypes().contains(DAGPENGER_GRUNNLAG));
        assertEquals(dagpenger, dtos.get(1).getGrunnlag());
    }

    @Test
    void when_beholdningGrunnlag_is_ufore_then_add_grunnlagTypeCode_UFORE_GRUNNLAG() {
        double ufore = 1d;

        BeholdningDto beholdning = constructBeholdningWithOpptjeningBelop(ufore, 0d, 0d, 0d, 0d, ufore);
        beholdning.getUforeOpptjeningBelop().setUforegrad(100);
        List<BeholdningDto> list = Collections.singletonList(beholdning);

        List<EndringPensjonsopptjeningDto> dtos =
                endringPensjonsbeholdningCalculator.calculateEndringPensjonsbeholdning(2020, list, new ArrayList<>());

        assertEquals(OPPTJENING, dtos.get(1).getArsakType());
        assertEquals(1, dtos.get(1).getGrunnlagTypes().size());
        assertTrue(dtos.get(1).getGrunnlagTypes().contains(UFORE_GRUNNLAG));
        assertEquals(ufore, dtos.get(1).getGrunnlag());
    }

    @Test
    void when_forstegangstjeneste_ufore_or_dagpenger_is_among_more_than_one_possible_grunnlag_then_add_all_grunnlagTypes_present_except_OMSORGSOPPTJENING() {
        double grunnlag = 7d;

        List<BeholdningDto> list = Collections.singletonList(
                constructBeholdningWithOpptjeningBelop(grunnlag, 2d, 3d, 1d, 4d, 5d));

        List<EndringPensjonsopptjeningDto> dtos =
                endringPensjonsbeholdningCalculator.calculateEndringPensjonsbeholdning(2020, list, new ArrayList<>());

        assertEquals(OPPTJENING, dtos.get(1).getArsakType());
        assertEquals(4, dtos.get(1).getGrunnlagTypes().size());
        assertTrue(dtos.get(1).getGrunnlagTypes().containsAll(List.of(FORSTEGANGSTJENESTE_GRUNNLAG, UFORE_GRUNNLAG, DAGPENGER_GRUNNLAG, INNTEKT_GRUNNLAG)));
        assertEquals(grunnlag, dtos.get(1).getGrunnlag());
    }

    @Test
    void when_beholdningGrunnlag_is_null_then_add_grunnlagTypeCode_NO_GRUNNLAG() {
        List<BeholdningDto> list = Collections.singletonList(
                constructBeholdningWithOpptjeningBelop(null, 0d, 0d, 0d, 0d, 0d));

        List<EndringPensjonsopptjeningDto> dtos =
                endringPensjonsbeholdningCalculator.calculateEndringPensjonsbeholdning(2020, list, new ArrayList<>());

        assertEquals(OPPTJENING, dtos.get(1).getArsakType());
        assertTrue(dtos.get(1).getGrunnlagTypes().contains(NO_GRUNNLAG));
    }

    @Test
    void when_beholdningGrunnlag_is_0_then_add_grunnlagTypeCode_NO_GRUNNLAG() {
        List<BeholdningDto> list = Collections.singletonList(
                constructBeholdningWithOpptjeningBelop(0.0, 0d, 0d, 0d, 0d, 0d));

        List<EndringPensjonsopptjeningDto> dtos =
                endringPensjonsbeholdningCalculator.calculateEndringPensjonsbeholdning(2020, list, new ArrayList<>());

        assertEquals(OPPTJENING, dtos.get(1).getArsakType());
        assertTrue(dtos.get(1).getGrunnlagTypes().contains(NO_GRUNNLAG));
    }

    private BeholdningDto constructBeholdningWithOpptjeningBelop(Double grunnlag, Double inntekt, Double omsorg, Double forstegangstjeneste, Double dagpenger, Double ufore) {
        BeholdningDto beholdning = new BeholdningDto();
        beholdning.setFomDato(LocalDate.of(2020, 1, 1));
        beholdning.setLonnsvekstregulering(new Lonnsvekstregulering());
        beholdning.setBelop(100d);
        beholdning.setBeholdningGrunnlag(grunnlag);

        InntektOpptjeningBelop inntektOpptjeningBelop = new InntektOpptjeningBelop();
        inntektOpptjeningBelop.setBelop(inntekt);
        beholdning.setInntektOpptjeningBelop(inntektOpptjeningBelop);

        OmsorgOpptjeningBelop omsorgOpptjeningBelop = new OmsorgOpptjeningBelop();
        omsorgOpptjeningBelop.setBelop(omsorg);
        beholdning.setOmsorgOpptjeningBelop(omsorgOpptjeningBelop);

        ForstegangstjenesteOpptjeningBelop forstegangstjenesteOpptjeningBelop = new ForstegangstjenesteOpptjeningBelop();
        forstegangstjenesteOpptjeningBelop.setBelop(forstegangstjeneste);
        beholdning.setForstegangstjenesteOpptjeningBelop(forstegangstjenesteOpptjeningBelop);

        DagpengerOpptjeningBelop dagpengerOpptjeningBelop = new DagpengerOpptjeningBelop();
        dagpengerOpptjeningBelop.setBelopOrdinar(dagpenger);
        beholdning.setDagpengerOpptjeningBelop(dagpengerOpptjeningBelop);

        UforeOpptjeningBelop uforeOpptjeningBelop = new UforeOpptjeningBelop();
        uforeOpptjeningBelop.setBelop(ufore);
        beholdning.setUforeOpptjeningBelop(uforeOpptjeningBelop);

        return beholdning;
    }
}
